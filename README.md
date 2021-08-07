# Nytimes-News
A demo Android app based on [The New York Times API](https://developer.nytimes.com/). The goal of the project is to integrate the latest Android tools and libraries for learning purposes. 

## Screenshots
<p>
<img src="/media/Screenshot_home.jpg" width="32%"/>
<img src="/media/Screenshot_search_result.jpg" width="32%"/>
<img src="/media/Screenshot_books.jpg" width="32%"/>
</p>
<p>
<img src="/media/Screenshot_article_details.jpg" width="32%"/>
<img src="/media/Screenshot_comments.jpg" width="32%"/>
<img src="/media/Screenshot_search.jpg" width="32%"/>
</p>

## Project Characteristics
The app was designed from the start to include some interesting aspects of 
Android development, namely:

* Multiple screens of the same flow: Both the [SearchFragment](https://github.com/ZianeA/Nytimes-News/blob/master/app/src/main/java/com/aliziane/news/articlesearch/search/SearchFragment.kt) and [SearchResultFragment](https://github.com/ZianeA/Nytimes-News/blob/master/app/src/main/java/com/aliziane/news/articlesearch/result/SearchResultFragment.kt) belong to the same flow. This was implemented using a shared ViewModel scoped to a navigation graph.

* Multiple back stacks: The app's primary navigation component is a bottom navigation bar. This was implemented using the latest version of the Jetpack Navigation Component which supports this functionality out of the box.

* ViewModel with runtime constructor arguments: Many ViewModels in this app require this, for example, [ArticleDetailsViewModel](https://github.com/ZianeA/Nytimes-News/blob/master/app/src/main/java/com/aliziane/news/articledetails/ArticleDetailsViewModel.kt). This was implemented using Dagger's assisted injection.

* Configuration changes and process death: Handled using Jetpack ViewModel and SavedStateHandle.

* One-off events (transient events): Implemented using Kotlin Flow.

* Passing data to previous destinations: An example of this is the [ArticleDetailsFragment](https://github.com/ZianeA/Nytimes-News/blob/master/app/src/main/java/com/aliziane/news/articledetails/ArticleDetailsFragment.kt) and [SortByDialog](https://github.com/ZianeA/Nytimes-News/blob/master/app/src/main/java/com/aliziane/news/articledetails/SortByDialog.kt). This was implemented using the Navigation Component NavBackStackEntry.

## Tech Stack
* MVVM Architecture
* Jetpack Navigation Component
* Coroutines and Flow
* Dagger
* JetPack ViewModel
* Retrofit
* Testing
  * JUnit 4
  * TestParameterInjector - For parameterized tests
  * Kotest Assertions Library
  * Turbine - For testing Kotlin Flows
