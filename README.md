Author: Aaron Costelloe
G00425705

Virtual Threaded Sentiment Analyzer README

Overview
The Virtual Threaded Sentiment Analyzer is a comprehensive Java application designed to perform sentiment analysis on text data. It utilizes virtual threads for efficient multitasking and a user-friendly menu-driven interface for ease of use.

Features
Menu-Driven Interface: Offers a simple and interactive console-based menu for navigating the application.
Concurrent File Parsing: Uses virtual threads for parsing text files line by line.
Sentiment Analysis: Analyzes the sentiment of text using a sentiment map and a set of stopwords.
Thread Safety: Ensures safe concurrent operations using thread-safe collections and synchronization techniques.
Output Logging: Results of the sentiment analysis are logged both on-screen and to a file.

Components
Runner: The main driver class that initializes the application and handles user interactions
MenuManager: Manages the user interface and input collection.
VirtualThreadFileParser: Parses the text files using virtual threads.
TweetSentimentAnalyzer: Conducts the sentiment analysis of the provided text.
SentimentScoreMapLoader: Loads and processes the sentiment map from a file.
FileManager: Handles file writing operations in a thread-safe manner.

How to Use
Start the Application: Run the Runner class. The menu will be displayed in the console.
Menu Options:
Specify a Text File: Provide the path to the text file containing the data for analysis.
Specify a Sentiment Map File: Provide the path to the sentiment map file.
Specify a Stopwords File: Provide the path to the stopwords file.
Perform Sentiment Analysis: Execute the sentiment analysis with the specified files.
Quit: Exit the application.
View Results: The sentiment analysis results are displayed in a dialog box and saved to the specified output file (Output/out.txt).

Prerequisites
Java (Version 11 or higher is recommended).
Ensure all necessary files (text, sentiment map, stopwords) are available and accessible.
Installation
Compile and run the Runner class using a Java IDE or command-line interface.


