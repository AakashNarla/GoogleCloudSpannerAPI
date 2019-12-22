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
import com.google.spanner.util.LoadCredentialsAPI
import groovy.util.logging.Slf4j
import io.grpc.StatusRuntimeException

import java.util.List
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
@Slf4j
public class InstanceAdminService {

	@Autowired
	LoadCredentialsAPI loadCredentialsAPI

	public InstanceConfig getInstanceConfig(final String url, final String my_config_id) {
		InstanceConfig instanceConfig
		try{
			if( my_config_id ) {
				instanceConfig = loadCredentialsAPI.getInstanceAdminClientCred(url).getInstanceConfig(my_config_id)
			}
		} catch(SpannerException ex) {
			if(ex.code == ErrorCode.NOT_FOUND.getCode()) {
				log.error("StatusRunTimeException, instance id :{} not found :{}",my_config_id, ex.getMessage())
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instance Not Found", ex);
			}
		} catch(Exception e) {
			log.error("Exception {}", e.detailMessage)
		}
		return instanceConfig
	}

	/** Example to list instance configs. */
	public List<InstanceConfig> listInstanceConfigs(final String url) {
		def configs
		try{
			configs = Lists.newArrayList(
					loadCredentialsAPI.getInstanceAdminClientCred(url).listInstanceConfigs(Options.pageSize(1)).iterateAll())
		} catch(SpannerException ex) {
			log.error("StatusRunTimeException, {}", ex.detailMessage)
		} catch(Exception e) {
			log.error("Exception {}", e.detailMessage)
		}
		return configs
	}

	/** Create an instance. */
	public String createInstance(final String url,
			final String instanceId, final String configId, final String clientProject) {

		if(instanceId && configId && clientProject ) {
			OperationFuture<Instance, CreateInstanceMetadata> op =
					loadCredentialsAPI.getInstanceAdminClientCred(url).createInstance(
					InstanceInfo.newBuilder(InstanceId.of(clientProject, instanceId))
					.setInstanceConfigId(InstanceConfigId.of(clientProject, configId))
					.setDisplayName(instanceId)
					.setNodeCount(1)
					.build())
					
			try {
				Instance v = op.get()
				return "Succesfully Created Instance Id : "+ v.getDisplayName()
			} catch (ExecutionException e) {
				throw (SpannerException) e.getCause()
			} catch (InterruptedException e) {
				throw SpannerExceptionFactory.propagateInterrupt(e)
			}
		}
	}

	/** Get an instance. */
	public Instance getInstance(final String url, final String my_instance_id) {
		Instance ins
		try{
			if( my_instance_id ) {
				ins = loadCredentialsAPI.getInstanceAdminClientCred(url).getInstance(my_instance_id)
			}
		} catch(SpannerException ex) {
			if(ex.code == ErrorCode.NOT_FOUND.getCode()) {
				log.error("StatusRunTimeException, instance id :{} not found :{}",my_instance_id, ex.getMessage())
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instance Not Found", ex);
			}
		} catch(Exception e) {
			log.error("Exception {}", e.detailMessage)
		}
		return ins
	}

	/** List instances. */
	public List<Instance> listInstances(final String url) {
		List<Instance> instances
		try {
			instances = Lists.newArrayList(loadCredentialsAPI.getInstanceAdminClientCred(url).listInstances(Options.pageSize(1)).iterateAll())
		}catch(Exception e) {
			log.error("Exception : {}", e.detailMessage)
		}
		return instances
	}

	/** Delete an instance. */
	public String deleteInstance(final String url, final String my_instance_id) {
		try{
			if( my_instance_id ) {
				loadCredentialsAPI.getInstanceAdminClientCred(url).deleteInstance(my_instance_id)
				return "Succesfully Deleted"
			}
		} catch(SpannerException ex) {
			if(ex.code == ErrorCode.NOT_FOUND.getCode()) {
				log.error("StatusRunTimeException, instance id :{} not found :{}",my_instance_id, ex.getMessage())
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instance Not Found", ex);
			}
		} catch(Exception e) {
			log.error("Exception {}", e.detailMessage)
		}
	}

	/** Update an instance. */
	public void updateInstance(final String url,
			Instance instance,
			final String clientProject,
			final String instanceId,
			final String newDisplayName) {


		InstanceInfo toUpdate =
				InstanceInfo.newBuilder(InstanceId.of(clientProject, instanceId))
				.setDisplayName(newDisplayName)
				.setNodeCount(instance.getNodeCount() + 1)
				.build()
		// Only update display name
		OperationFuture<Instance, UpdateInstanceMetadata> op =
				loadCredentialsAPI.getInstanceAdminClientCred(url).updateInstance(toUpdate, InstanceInfo.InstanceField.DISPLAY_NAME)
		try {
			op.get()
		} catch (ExecutionException e) {
			throw (SpannerException) e.getCause()
		} catch (InterruptedException e) {
			throw SpannerExceptionFactory.propagateInterrupt(e)
		}
	}
}
