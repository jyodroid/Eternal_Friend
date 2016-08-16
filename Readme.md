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

### Development

Want to contribute? Great!

Dillinger uses Gulp + Webpack for fast developing.
Make a change in your file and instantanously see your updates!

Open your favorite Terminal and run these commands.

First Tab:
```sh
$ node app
```

Second Tab:
```sh
$ gulp watch
```

(optional) Third:
```sh
$ karma start
```
#### Building for source
For production release:
```sh
$ gulp build --prod
```
Generating pre-built zip archives for distribution:
```sh
$ gulp build dist --prod
```
### Docker
Dillinger is very easy to install and deploy in a Docker container.

By default, the Docker will expose port 80, so change this within the Dockerfile if necessary. When ready, simply use the Dockerfile to build the image.

```sh
cd dillinger
npm run-script build-docker
```
This will create the dillinger image and pull in the necessary dependencies. Moreover, this uses a _hack_ to get a more optimized `npm` build by copying the dependencies over and only installing when the `package.json` itself has changed.  Look inside the `package.json` and the `Dockerfile` for more details on how this works.

Once done, run the Docker image and map the port to whatever you wish on your host. In this example, we simply map port 8000 of the host to port 80 of the Docker (or whatever port was exposed in the Dockerfile):

```sh
docker run -d -p 8000:8080 --restart="always" <youruser>/dillinger:latest
```

Verify the deployment by navigating to your server address in your preferred browser.

```sh
127.0.0.1:8000
```

#### Kubernetes + Google Cloud

See [KUBERNETES.md](https://github.com/joemccann/dillinger/blob/master/KUBERNETES.md)


#### docker-compose.yml

Change the path for the nginx conf mounting path to your full path, not mine!

### N|Solid and NGINX

More details coming soon.


### Todos

 - Write Tests
 - Rethink Github Save
 - Add Code Comments
 - Add Night Mode

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
