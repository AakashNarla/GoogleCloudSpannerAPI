# GoogleCloudSpannerAPI

### Guides To Executing the program
To Clean and Build:
``` .\gradlew clean build ```

To Run the application:
``` .\gradlew bootRun ```


### Get Access tokne from Google Cloud Spanner API
* Go to [Oauth2.0 Playground](https://developers.google.com/oauthplayground/) 
* Select Google Spanner API scope as admin or just paste "https://www.googleapis.com/auth/spanner.admin" in textbox 
* Click on Authorize API to get the access token. 

Note: Access token is only available for 60 min
