Markdown++ (or MD++)
-----------

Learn more about the MD++ language and this converter by reading the FAQs, or jump right in with the Usage Instructions (bottom of this page).

-----------

## The FAQs:
### Why?
Markdown++ provides a section-oriented markup language while retaining key features of the standard Markdown markup language. Notably, Markdown++ supports sections (analogous to HTML `<div>`s) and an extended repitoire of styling options for text. Nonetheless, Markdown++ retains the plain-text nature of the Markdown language throughout most components, accelerating the documentation-writing process while providing valuable control for those who care about the style of their documents.

### What is the formal definition of the language?
See the formal language specification at https://danielmayer.me/mdpp/specification.html.

### How can I use Markdown++?
You can run the MD++-to-HTML converter (written in Java) contained within this project (see `Converter Instructions`).

Alternatively, you can write your own converter/parser/editor by following the specification. Please let me know if you do so!

### Are there any example Markdown++ files?
Yes! See the source of the official specification or the source for my website.

### How can I ask questions?
Contact daniel@danielmayer.me or visit danielmayer.me/contact.

### How can I contribute?
Submit a pull request with your proposed changes.

## Convertor Instructions:
Either compile the project using JDK 21+ or download the pre-compiled JAR file and run it with `java -jar mdpp-conv`.

Once you have a JAR/class file, run `the-executable from-directory to-directory`, where `from-directory` is a directory of MD++ (and/or other files) that you want to convert to HTML files and `to-directory` is where those HTML files should go. Example: `java -jar mdpp-conv.jar C:/Fake Path/Source C:/Fake Path/Dest`. 