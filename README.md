# Popular Movies

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

