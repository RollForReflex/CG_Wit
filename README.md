CG_Wit
======

Entry for the Deep Web Hackathon/first attempt at Android/Java dev

The goal of this application is to leverage the Wit (http://www.wit.ai) service to generate an AI application that will run on Android devices that gives deeper understanding as to the themes in Childish Gambino's Because the Internet album.

With the combination of this application using pulled JSON from a dynamic source as JSON and the Wit service, we're able to dynamically set up a responsive AI to certain key words/phrases determined on a neural-network based threshold, a common technique in Natural Language Processing (NLP)

The app itself is very basic because I spent most of this time trying to play with Android for the first time since it's what I thought would be best to get on a mobile device. I plan to redo this using web technologies and Node.JS since I am not even proficient in Java but thought this would be a good learning experience.

The Setup
======

We make a call out to Wit like so here:
https://wit.ai/docs/images/intro/console.png

and set up Intents and Entities, or certain ways the backend service should read certain/similar sentences and what kind of data to return with.

In the app, we set up the UI view as well as pulling down the JSON (currently hosted on my website but could come from any source) and parse that into a set of classes that when Wit returns an intent with possible entities, we can deliver a human-like response.

Why
======

The point of this app was to act as a Siri-like representation of the Boy's "Mother" which is essentially the internet that raised him. Hopefully y'all enjoy.
