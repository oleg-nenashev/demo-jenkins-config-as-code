Locale Plugin for Jenkins
=========================

This plugin controls the language of Jenkins

Normally, Jenkins honors the browser's language preference
if a translation is available for the preferred language,
and uses the system default locale for messages during a build.

This plugin allows you to:

* override the system default locale to the language of your choice
* ignore browser's language preference completely

This feature is sometimes convenient for multi-lingual environment.

### Usage
Under _Manage Jenkins > Configure System_ there should be a "Locale" section.

Here you can enter the _Default Language_: this should be a language code
or locale code like "fr" (for French), or "de_AT" (German, in Austria).

This value will be used by the system, for example, for messages that are printed
to the log during a build (assuming that the Jenkins features and plugins that
you're using have been translated into the specified language).

To additionally force this language on all users, overriding their browser language,
you can check the "Ignore browser preference and force this language to all users" option.
