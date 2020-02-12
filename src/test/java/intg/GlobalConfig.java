package intg;

public interface GlobalConfig {
     String baseUrl = "http://localhost:8080/";
     String baseInstanceUrl = baseUrl.concat("/v1/spanner/instance/");
     String baseDatabaseUrl = baseUrl.concat("/v1/spanner/{instance-id}/databases/");

     String baseTableUrl = baseUrl.concat("/v1/spanner/{instance-id}/{database}/{table}/");

     String baseTableQueryUrl = baseUrl.concat("/v1/spanner/{instance-id}/{database}/query/table");

     String credentialsUrl = "https://raw.githubusercontent.com/AakashNarla/GoogleCloudSpannerAPI/master/helical-fin-valid.json";
}
