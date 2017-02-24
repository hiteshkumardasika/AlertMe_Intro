# AlertMe
AlertMe
Our App is used to Alert user whenever we are near our destination.
So first, user gives a destination where you are heading at. We show up all possible routes.

![Alt text](https://github.com/hiteshkumardasika/AlertMe_Intro/blob/master/screens/Screenshot_2017-02-23-20-31-14.jpg "First")
![Alt text](https://github.com/hiteshkumardasika/AlertMe_Intro/blob/master/screens/Screenshot_2017-02-23-20-31-23.jpg "Second")
![Alt text](https://github.com/hiteshkumardasika/AlertMe_Intro/blob/master/screens/Screenshot_2017-02-23-20-31-38.jpg "Second")
![Alt text](https://github.com/hiteshkumardasika/AlertMe_Intro/blob/master/screens/Screenshot_2017-02-23-20-31-43.jpg "Second")
![Alt text](https://github.com/hiteshkumardasika/AlertMe_Intro/blob/master/screens/Screenshot_2017-02-23-20-31-50.jpg "Second")
![Alt text](https://github.com/hiteshkumardasika/AlertMe_Intro/blob/master/screens/Screenshot_2017-02-23-20-32-05.jpg "Second")
![Alt text](https://github.com/hiteshkumardasika/AlertMe_Intro/blob/master/screens/Screenshot_2017-02-23-20-32-23.jpg "Second")
![Alt text](https://github.com/hiteshkumardasika/AlertMe_Intro/blob/master/screens/Screenshot_2017-02-23-20-38-20.jpg "Second")
![Alt text](https://github.com/hiteshkumardasika/AlertMe_Intro/blob/master/screens/Screenshot_2017-02-23-20-38-50.jpg "Second")



























The app opens up with our current location              Clicking that icon allows user to add destination


























	






























































Above Mentioned are Screenshots of Our App

We mainly used two permissions
ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION. Both help us in getting an accurate location of the source by using different providers like gps and network.

Imporant Classes and Methods

There are two classes
MainActivity:
This class implements OnMapReadyCallBack interface which helps us to get control of the activity after the map is loaded.
This class also implements a package called RoutingListener which uses google Directons API to get distance between places given their latitude and longitude.
We have also implemented locationListener which has onLocationChanged which detects the location change.
Settings
This class is used to take buffer radius to alert the user and also the userName.

