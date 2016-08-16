# Eternal Friend

Eternal Friend is a tool to keep your pets closer and take care about their needs. In order to achieve this, its offers the possibility of:

  - Add several pets profiles with name, breed and birth date.
  - Create a gallery with the option of age classification so you can see how your friend changes through time.
  - Create a History of clinical attendances of your friend
  - Create a list of your pets vaccinations. 
  - Check Vaccination that already are applied.
  - Receive a daily reminder of pending vaccinations. 
  - Using a widget to se latest news about pets in The New York Times.
  - See nearest pet shops and veterinarian in a map.

### Tech
The app works with different android design patterns and architecture implementations as:

- **Content provider** for data persistence.
- **Cursor loaders** for queries on data layer.
- **SyncAdapter** for bring periodically data from server.
- **Permissions** are required at runtime for post marshmallow devices.
- **Services** to perform blocking executions.
- **Alarm manager** to perform notification in time intervals.
 
The app also uses some services to obtain relevant information to the app as:

- **Google Maps API** to create a custom map activity.
- **Google Places API** to place relevant point in map.
- **The New York Times Web API** to fetch latest news about pets. 

Besides Android dependencies, this app uses different libraries that are defined in *build.gradle* of module. This libraries are: 

* [ButterKnife] - Used to bind views to activities.
* [Volley] - Used for make http requests.
* [Gson] - used for serialize data form server.
* [Glide] - Image loader.
* [Wasabeef glide-transformation] - Transformations to complement glide.

And of course Dillinger itself is open source with a [public repository][dill]
 on GitHub.

### Installation

First you need to install gradle. see instructions from [Gradle official page](https://gradle.org/).

now you need to add three strings constants in a strings resources that contain the respective keys of the APIs. The name of the keys resources in app are:
- ```google_maps_key``` obtain from Google Developer Console as in the [official documentation](https://developers.google.com/maps/documentation/android-api).
- ```google_places_key``` obtain from Google Developer Console as in the [official documentation](https://developers.google.com/places/web-service)
- ```nyt_api_key```obtain from [The New York Time developer page](https://developer.nytimes.com/)
 
finally runs for debug version gradle command:
```sh
$gradle installDebug
```
Or for release version gradle command:
```sh
$gradle installRelese
```
And ensures you have the current signature for the application.

### Screenshots
Profiles view
![profile][profile]

Delete Profile
![delete swipe][swipe]
![delete swipe][delete]

Profile details
![profile details][profile_details]



[profile]: /screenshots/profile.png
[swipe]: /screenshots/profile_swipe.png
[delete]: /screenshots/delete_profile.png
[profile_details]: /screenshots/profile_details.png

### Todos

 - Add support to pre - lollipop devices
 - Add support for other screen devices
 - add phone number on points in map
License
----

MIT


**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [ButterKnife]: <http://jakewharton.github.io/butterknife/>
   [Volley]: <https://developer.android.com/training/volley/index.html>
   [GSON]: <https://github.com/google/gson>
   [Glide]: <https://github.com/bumptech/glide>
   [Wasabeef glide-transformation]: <https://github.com/wasabeef/glide-transformations>
