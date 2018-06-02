# DBWeather

## Project Overview
Android multimedia application with weather forecasts, a news feed in every language , and Live TV using youtube live.

## Features

### Weather

Provides localized and detailed Weather reports based on the user's location:
<p float="left">
  <img title="Weather Clear" alt="Weather Clear" src="weather_clear.png" hspace="8" width="270" height="480" />
  <img title="Weather with rain" alt="Weather with rain" src="weather_rain.png" hspace="8" width="270" height="480" /> 
  <img title="Weather with rain 2" alt="Weather with rain 2" src="weather_rain2.png" hspace="8" width="270" height="480" />
</p>
### News Feed

Provides a feed to current world events and other important news:

<p float="left">
  <img title="News Feed Feature" alt="News Feed Feature" src="articles.png" hspace="8" width="270" height="480" />
  <img title="News Detail Feature" alt="News Detail Feature" src="articlesdetail.png" hspace="8" width="270" height="480" /> 
  <img title="News Feed Feature" alt="News Feed Feature" src="articles2.png" hspace="8" width="270" height="480" />
</p>

### Live TV:

Browse and View Live News Channels via Youtube Live:

<p float="left">
  <img title="Live TV Feature" alt="Live TV Feature" src="lives.png" hspace="50" width="270" height="480" />
  <img title="Live TV Detail Feature" alt="Live TV Detail Feature" src="livedetail.png" hspace="50" width="270" height="480" /> 
</p>

<img src="livedetailfullscreen.png" alt="Live TV Detail FullScreen Feature" width="960" height="480"/>

## Project Structure:

DBWeather utilizes a modular style of project structure, which divides the project into Modules based on Features and Layers of the Application's Architecture. Such modules include:

### app

Front end View implementations and Android Framework Classes. Presenters and Contracts for the frond end are defined in separate modules.

### dbweathercommon:

Global high level Interfaces and Utilities.

### dbweatherdata:

Backend implementations to local and remote Datasources. Keeping them in a separate module increases modularity and separation of concerns. 

### dbweatherdomain:

Contains Entities based on the Problem Domain, and high level contracts which define the repositories implemented in the data module.

### dbweathernews:

Contains logic and contracts which define the front end of the News Feed and Live TV features. 
 
### dbweatherweather:

Contains logic and contracts which define the front end of the Weather feature. 

## Project Architecture

DBWeather utilizes a highly modular Clean Architecture which is based on ideas from multiple styles of Architecture, including MVP, MVVM, and MVI. The goal of these principles is to encourage:
- Decoupling/Separation of Concerns by dividing Presentation, Domain, and Data logic/contracts from their respective interfaces. This allows different modules of the project to be changed, without necessitating changes in other modules. 
- High Legibility due to proper separation of concerns, which leads to descriptive and unambiguous names (Weather, News, etc.).

## Tools and APIs:

DBWeather uses a wide array of industry standard APIs in places where it makes sense to use them. Such APIs include (but not limited to):
- Dagger 2 for Dependency Injection
- Android's AAC (Room) for Data Persistence
- Retrofit 2 + OKHttp for Network IO
- RxJava 2 for concurrency and event logic

## Download app at
https://play.google.com/store/apps/details?id=com.dbeginc.dbweather

## Contact Info

https://twitter.com/darel_bitsy

https://www.facebook.com/darel.bitsy

https://github.com/bitsydarel

