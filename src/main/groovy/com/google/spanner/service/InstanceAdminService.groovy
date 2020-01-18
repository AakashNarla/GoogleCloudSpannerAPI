package com.google.spanner.service

import com.google.api.gax.longrunning.OperationFuture
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.ErrorCode
import com.google.cloud.spanner.Instance
import com.google.cloud.spanner.InstanceAdminClient
import com.google.cloud.spanner.InstanceConfig
import com.google.cloud.spanner.InstanceConfigId
import com.google.cloud.spanner.InstanceId
import com.google.cloud.spanner.InstanceInfo
import com.google.cloud.spanner.Options
import com.google.cloud.spanner.Spanner
import com.google.cloud.spanner.SpannerException
import com.google.cloud.spanner.SpannerExceptionFactory
import com.google.cloud.spanner.SpannerOptions
import com.google.common.collect.Lists
import com.google.spanner.admin.instance.v1.CreateInstanceMetadata
import com.google.spanner.admin.instance.v1.UpdateInstanceMetadata
import com.google.spanner.exception.ResourceNotFoundException
import com.google.spanner.util.LoadCredentialsAPI
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import io.grpc.StatusRuntimeException

import java.util.List
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
@Slf4j
 class InstanceAdminService {

    @Autowired
    LoadCredentialsAPI loadCredentialsAPI

    private Spanner spanner

    InstanceAdminClient getInstanceAdminClientCred(String url) {
        try {
            spanner = loadCredentialsAPI.getSpanner(url)
        } catch (Exception e) {
            throw e
        }
        return spanner?.getInstanceAdminClient()
    }

     InstanceConfig getInstanceConfig(final String url, final String my_config_id) {
        InstanceConfig instanceConfig = null
        try {
            if (my_config_id) {
                instanceConfig = getInstanceAdminClientCred(url).getInstanceConfig(my_config_id)
            }
        } catch (SpannerException ex) {
            if (ex.code == ErrorCode.NOT_FOUND.getCode()) {
                log.error("StatusRunTimeException, instance id :{} not found :{}", my_config_id, ex.getMessage())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instance Not Found", ex)
            }
        } catch (Exception e) {
            log.error("Exception {}", e.message)
        } finally {
            spanner?.close()
        }

        return instanceConfig
    }

    /** List all instance configs. */
     List<InstanceConfig> listInstanceConfigs(final String url) {
        def configs = null
        try {
            configs = Lists.newArrayList(
                    getInstanceAdminClientCred(url).listInstanceConfigs(Options.pageSize(1)).iterateAll())
        } catch (SpannerException ex) {
            log.error("StatusRunTimeException, {}", ex.message)
        } catch (Exception e) {
            log.error("Exception {}", e.message)
        } finally {
            spanner?.close()
        }

        return configs
    }

    /** Create an instance. */
     String createInstance(final String url,
                                 final String instanceId, final String configId, final int node) {
         final String clientProject = loadCredentialsAPI.getProjectFromCredentials(url)
        if (instanceId && configId && clientProject && node) {
            try {
                OperationFuture<Instance, CreateInstanceMetadata> op =
                        getInstanceAdminClientCred(url).createInstance(
                                InstanceInfo.newBuilder(InstanceId.of(clientProject, instanceId))
                                        .setInstanceConfigId(InstanceConfigId.of(clientProject, configId))
                                        .setDisplayName(instanceId)
                                        .setNodeCount(node)
                                        .build())

                Instance v = op.get(1, TimeUnit.MINUTES)
                return "Successfully Created Instance Id : " + v.getDisplayName()
            } catch (ExecutionException e) {
                throw (SpannerException) e.getCause()
            } catch (InterruptedException e) {
                throw SpannerExceptionFactory.propagateInterrupt(e)
            } finally {
                spanner?.close()
            }
        } else {
            throw new ResourceNotFoundException("Missing Parameters", HttpStatus.NOT_FOUND.value(), "Important Parameters are missing")
        }
    }

    /** Get an instance. */
     Instance getInstance(final String url, final String my_instance_id) {
        Instance ins = null
        try {
            if (my_instance_id) {
                ins = getInstanceAdminClientCred(url).getInstance(my_instance_id)
            }
        } catch (SpannerException ex) {
            if (ex.code == ErrorCode.NOT_FOUND.getCode()) {
                log.error("StatusRunTimeException, instance id :{} not found :{}", my_instance_id, ex.getMessage())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instance Not Found", ex)
            }
        } catch (Exception e) {
            log.error("Exception {}", e.message)
        } finally {
            spanner?.close()
        }

        return ins
    }

    /** List All Instances. */
     List<Instance> listInstances(final String url) {
        List<Instance> instances = null
        try {
            instances = Lists.newArrayList(getInstanceAdminClientCred(url).listInstances(Options.pageSize(1)).iterateAll())
        } catch (Exception e) {
            log.error("Exception : {}", e.message)
            throw e
        } finally {
            spanner?.close()
        }

        return instances
    }

    /** Delete an instance. */
     String deleteInstance(final String url, final String my_instance_id) {
         String deleteInstance = null
        try {
            if (my_instance_id) {
                getInstanceAdminClientCred(url).deleteInstance(my_instance_id)
                deleteInstance = "Successfully Deleted"
            }
        } catch (SpannerException ex) {
            if (ex.code == ErrorCode.NOT_FOUND.getCode()) {
                log.error("StatusRunTimeException, instance id :{} not found :{}", my_instance_id, ex.getMessage())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instance Not Found", ex)
            }
        } catch (Exception e) {
            log.error("Exception {}", e.message)
            throw e
        } finally {
            spanner?.close()
        }
         return deleteInstance
    }

    /** Update an instance. */
    String updateInstance(final String url, final String instanceId, final String newDisplayName, final int nodeCount) {
        final String clientProject = loadCredentialsAPI.getProjectFromCredentials(url)
        String finalResult = null
        Instance oldInstance = getInstance(url, instanceId)
        if (oldInstance) {
            InstanceInfo toUpdate =
                    InstanceInfo.newBuilder(InstanceId.of(clientProject, instanceId))
                            .setDisplayName(newDisplayName)
                            .setNodeCount(nodeCount)
                            .build()
            OperationFuture<Instance, UpdateInstanceMetadata> op
            if (oldInstance.getDisplayName() != newDisplayName && oldInstance.getNodeCount() != nodeCount) {
                op = getInstanceAdminClientCred(url).updateInstance(toUpdate, InstanceInfo.InstanceField.DISPLAY_NAME, InstanceInfo.InstanceField.NODE_COUNT)
            } else if (oldInstance.getDisplayName() != newDisplayName) {
                op = getInstanceAdminClientCred(url).updateInstance(toUpdate, InstanceInfo.InstanceField.DISPLAY_NAME)
            } else if (oldInstance.getNodeCount() != nodeCount) {
                op = getInstanceAdminClientCred(url).updateInstance(toUpdate, InstanceInfo.InstanceField.NODE_COUNT)
            } else {
                return "Update Not Required"
            }

            try {
                Instance v = op.get(1, TimeUnit.MINUTES)
                finalResult = "Successfully Updated Instance : " + v.getDisplayName()
            } catch (ExecutionException e) {
                throw (SpannerException) e.getCause()
            } catch (InterruptedException e) {
                throw SpannerExceptionFactory.propagateInterrupt(e)
            } finally {
                spanner?.close()
            }
        } else {
            throw new ResourceNotFoundException("Instance Not Found", HttpStatus.NOT_FOUND as int, "Instance Not found for Config id : " + instanceId)
        }

        return finalResult
    }

    static InstanceInfo.InstanceField[] getInstanceField(Instance oldInstance) {
        InstanceInfo.InstanceField
        return InstanceInfo.InstanceField.DISPLAY_NAME
    }
}
