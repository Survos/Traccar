survey-tracker, based on traccar-client

Web page - http://www.traccar.org and www.survos.com

Author - Tac Tacelosky (tac@survos.com)
Author - Anton Tananaev (anton.tananaev@gmail.com)

SUMMARY:

Survos Tracker is a repackaged version of the Traccar Client is Android GPS tracking application.
It has the following difference:

* Additional fields are sent to the server, including gps precision, location provider and local time.
* Eventually those fields will be sent as JSON and queued in SQLite if they aren't received.
* Better error messages when a connection hasn't been established.


TeliTrax Environment set up

For that you can read here in detail : https://developer.android.com/sdk/installing/index.html

Publishing app Follow below links 

http://forumone.com/insights/how-publish-your-mobile-app-google-play-and-apple-app-stores/

http://developer.xamarin.com/guides/android/deployment,_testing,_and_metrics/publishing_an_application/part_2_-_publishing_an_application_on_google_play/



Updating version number for app and uploading newer version to playstore.

Open Projects in Android Studio.

Then open file named as “build.gradle” file in side “”Traccar-client folder

Change version number and code you want to upload 

defaultConfig {
        minSdkVersion 14
        targetSdkVersion 21
        applicationId 'com.survos.telitrax'
        versionCode 1
        versionName "1.0"
    }


Don’r forgot to save file before building new api.

Now Select Build from Top menu. 

from that selected “Generate Signed APK”

You will see pop up like this : http://cl.ly/image/3Y1J1G2I1y0R

Then flow like this : http://cl.ly/image/1r1X0V0c3W3w

Select keystone file telitrax.jks and its password set to 123456

You will see pop up like this : http://cl.ly/image/1r2C3o1q0H3f

Wait it here for few min it will take time to generate apk.

You will see pop up like this : http://cl.ly/image/420I182I3F1h

Lets get apk from that and upload it to playstore.

Follow same for next version update.





LICENSE:

Apache License, Version 2.0

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

