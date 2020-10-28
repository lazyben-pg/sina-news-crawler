# IO实战: 使用多种方法读写文件

请完成[`FileAccessor`](https://github.com/hcsp/read-write-files/blob/master/src/main/java/com/github/hcsp/io/FileAccessor.java)中的程序，使用至少三种方法将指定文件读取成若干行文本，并使用至少三种方法将多行文本写入指定文件。你可以选择以下方法：

- 使用第三方库，例如Apache Commons IO
- 使用最原始的`FileInputStream`/`FileOutputStream`，一个字符一个字符的读取
- 使用`BufferReader`/`BufferedWriter`，一行一行地读写文件
- 使用Java 7+的NIO引入的`Files.readAllBytes()`/`Files.readAllLines()`/`Files.write()`方法

你看，完成一件事情有很多种方法，我们希望你了解并学会使用更多的方法。

-----
注意！我们只允许你修改以下文件，对其他文件的修改会被拒绝：
- [src/main/java/com/github/hcsp/io/FileAccessor.java](https://github.com/hcsp/read-write-files/blob/master/src/main/java/com/github/hcsp/io/FileAccessor.java)
- [pom.xml](https://github.com/hcsp/read-write-files/blob/master/pom.xml)
-----


完成题目有困难？不妨来看看[写代码啦的相应课程](https://xiedaimala.com/tasks/661cd7ab-7fea-47d0-8e11-555d6fca751d)吧！

回到[写代码啦的题目](https://xiedaimala.com/tasks/661cd7ab-7fea-47d0-8e11-555d6fca751d/quizzes/6c87ef57-7f06-4af2-9112-86dd27ff099d)，继续挑战！
