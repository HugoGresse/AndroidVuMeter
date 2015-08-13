## Android VuMeter library

A simple fake **VuMeter** (or equalizer) for Android that contain only two class and display the VuMeter with canvas and not views. 

Features
--------

- customisable bar **color**, **spacing**, **speed**, **stopSize** and bar **number**
- available xml attributes and getters/setters
- match view given size and padding
- **stop**, **resume** and **play** method

![demo](https://github.com/HugoGresse/AndroidVuMeter/blob/master/demo.gif)

Usage
-----
```xml
    <io.gresse.hugo.vumeterlibrary.VuMeterView
        android:layout_width="300dp"
        android:layout_height="300dp"
        android:paddingLeft="20dp"
        android:paddingRight="20dp"
        android:paddingBottom="40dp"
        android:id="@+id/vumeter"
        vumeter:stopSize="5dp"
        vumeter:speed="10"
        vumeter:blockNumber="5"
        vumeter:blockSpacing="20dp"
        vumeter:backgroundColor="#33b5e5"/>
```

See [DemoApp](https://github.com/HugoGresse/AndroidVuMeter/blob/master/app/src/main/java/io/gresse/hugo/vumeter/MainActivity.java) for complete usage example.

See [wiki](https://github.com/HugoGresse/AndroidVuMeter/wiki) for the available methods. 

Download
--------

Using Gradle:
```
compile 'io.gresse.hugo.vumeterlibrary:vumeterlibrary:1.0.9'
```

*Hosted on jCenter only.* 

Author
------
[Hugo Gresse](http://hugo.gresse.io)


License
--------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
