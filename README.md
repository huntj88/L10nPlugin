# L10nPlugin

This is a gradle plugin which takes your strings.xml file in an Android application or library project,
and generates code to allow you use your localized strings in the language specified by the users Android device. 

L10n is able to do this without having to have access to a context, or writing your own abstraction. 
One reason why someone might want to write their own abstraction is so that they can use their strings 
in their presenters/viewModels without breaking their jvm unit tests. Even if your app only supports one language you still benefit.

With L10n the entire abstraction is generated on the fly, and you are free to use it anywhere in your code.



## Installation Instructions

Add this to your top level build.gradle file in the buildscript section under repositories.

`maven { url 'https://jitpack.io' }`


Then add this to your buildscript dependencies.

`classpath 'com.github.huntj88:L10nPlugin:v0.9.0-beta'`


Finally, apply the plugin to any Android application or library project at the top of the build.gradle file with the other plugins

`apply plugin: 'L10n'`

Now use it by simply calling 
`L10n.<string_name>`

Here is a screenshot of the generated api, and how to use L10n within your code.

![generated code](https://raw.githubusercontent.com/huntj88/L10nPlugin/master/images/generatedCodeAndUsage.png)

Looking through the generated code is encouraged, to get a quick understanding of how its works. Its very simple.

## Upcoming features
- On some projects the strings.xml files are not in the default location, or are named something different. I will provide a way to configure L10n to use those files.
- Plurals


## Unsupported Features
L10n does not currently support:
- plurals
- HTML elements inside the strings

all of these can still be used the conventional way



## FAQ's
[What is the L10n standard?](https://blog.mozilla.org/l10n/2011/12/14/i18n-vs-l10n-whats-the-diff/)

[Where did the inspiration come from?](https://github.com/SwiftGen/SwiftGen#strings)



## Feedback
This project has been tested on a project with more than 500 string resources, and I've caught all the edge cases in that project (except for what is listed in
unsupported features), but it's likely that there may be a few more things I need to handle. If you have any feedback regarding things that don't work I'd like to hear it.

Ditto for any pull requests, or even general code reviews