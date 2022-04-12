# Assignment 1
The task of this assignment is to develop a **Web-Crawler in Java which provides a compact overview of the given website and linked websites in a given language by only listing the headings and the links.**
The attached **example-report.md** shows an example on how the overview should look like (feel free to make improvements upon the suggested layout).

## Must have features
The crawler MUST implement at least the following features:
* input the URL, the depth of websites to crawl, and the target language as command line arguments
* create a compact overview of the crawled websites in a specified target language
  * record and translate only the headings
  * represent the depth of the crawled websites with proper indentation (see example)
  * also record the URLs of the crawled sites
  * highlight broken links
* find the links to other websites and recursively do the analysis for those websites (it is enough if you analyze the pages at a depth of 2 without visiting further links, you might also allow the user to configure this depth via command line)
* store the results in single mark down file (.md extension)

**Note**, also provide automated unit tests for each feature (we will not accept submissions without unit tests).

## Implementation
Regarding the **implementation**, please use a modern IDE (Eclipse, IntelliJ, Visual Code, etc. ) and GitHub, GitLab, or BitBucket to version and share your sources.
The repository needs to contain a **README** file that briefly states the steps to build, run, and test your crawler.
Use a Java testing framework, e.g., JUnit for automating the tests. Also use a build tool like Maven or Gradle to automate the build and testing of your solution. 
Furthermore, we suggest to use an existing library, such as [jsoup](https://jsoup.org/), for parsing html. 
For translating the headings you can use a REST API such as provided by [DeepL](https://www.deepl.com/translator).

## Submission
Submit a pdf file (1 A4-page) that contains the link to your project repository (GitHub, GitLab, BitBucket etc.) and the names of your group members via Moodle. 
The deadline is: **25. April 2022, 23:59**

## Grading
The assignment will be evaluated based on the following criteria:

* How well you named functions, variables, classes (how descriptive the code is)
* The functions you implemented and if they follow certain clean code principles (single responsibility etc.)
* How well your code is tested and how clean Unit tests are written
* The overall structure of the code (formatting).
* Correct and appropriate use of comments.

Keep in mind that the 2nd Assignment will build upon this one, so do yourself a favor and write clean code.

##Questions
Please use the "Diskussionsforum" to post and discuss your questions.