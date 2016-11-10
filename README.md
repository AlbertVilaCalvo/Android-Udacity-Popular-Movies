# Popular Movies

An Android app that displays movies. It's the project Popular Movies (Stage 2) of the Udacity Android Developer Nanodegree.

It uses libraries like Dagger 2, Retrofit, RxJava and AutoValue. It's built using the Model-View-Presenter pattern. It caches data locally using an SQLite database and the libraries SQLBrite and SQLDelight.

It has a master-detail UI adapted for tablets.

## The Movie DB API Key

To use the app you'll need an API Key for [www.themoviedb.org](https://www.themoviedb.org/). The app
expects an `string` resource named `movie_db_api_key` with the API key as it's value. Add a file
(which could be named, for example, `keys.xml`) into the folder `app/src/main/res/values` with an
`string` resource, like this:

```xml
<resources>
    <string name="movie_db_api_key">c349e4d167ged7yge44d760630cf7e1</string>
</resources>
```

## Screenshots

#### Main Activity

![Main Activity](/screenshots/feature-MainActivity-11102016125237.png?raw=true "Main Activity")

<p></p>

#### Detail Activity

![Detail Activity](/screenshots/feature-MainActivity-11102016125308.png?raw=true "Detail Activity")

<p></p>

#### Master Detail

![Master Detail](/screenshots/Screenshot_2016-11-10-12-53-44.png?raw=true "Master Detail")
