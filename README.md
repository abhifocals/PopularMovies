# Popular Movies
This app allows users to discover the most popular movies playing.

# Features
* Users can sort by most popular or by top rated.
* Information provided on movie screen:
    * original title
    * movie poster image thumbnail
    * A plot synopsis (called overview in the api)
    * user rating (called vote_average in the api)
    * release date
* Users can view and play trailers ( either in the youtube app or a web browser).
* Users can also mark a movie as a favorite in the details view by tapping 'Add Favorite' button.

# Usage
Provide your themoviedb.org Api Key in local gradle.properties like so:
`movieDBApiToken="Your Key Here"` else you'll get a null response.