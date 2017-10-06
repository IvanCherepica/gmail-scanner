<h3>How to use</h3>
<p>To begin with, you should populated the properties as shown bellow:</p>
<h6>
  sender=sender@gmail.com</br>
  answers.amount=5</br>
  right.answers=1,2,3,4,5</br>
  own.addres=yourAdress@gmail.com</br>
  password=yourPassword</br>
  received.mail.subject=title</br>
  spreadsheet.url=https://docs.google.com/spreadsheets/d/***/edit#gid=0</br>
</h6>
<p>Then create a txt file with name "explanations.txt" in the root with similar content:</p>
<h6>
  Заголовок ответа: "Результаты теста"

  1) Пояснения по первому вопросу.

  2) Пояснения по второму вопросу.

  3) Пояснения по третьему вопросу.

  4) Пояснения по четвёртому вопросу.

  5) Пояснения по пятому вопросу.
</h6>
<p>Run the application.</p>
<h2>To build a jar file</h2>
<ol>
  <li>Open the console and switch to the directory with downloaded unarchive zip file from
  GitHub (or any other folder in which you want to create a jar file). 
  Use "cd" command for changing directory. For example: "cd \myapp". 
  For the changing disc space just pointed "c:" without command. 
  In another case you can just right-click on the folder you need with pressed Shift and select 
  "Open the command line here" in the pop-up menu. </li>
  <li>Setup path to JDK bin folder in environment variables if it's not already done. Or use "path" command like this 
"path c:\Program Files\Java\jdk1.5.0_09\bin".</li>
  <li>Create a jar file via "jar" command. For example, you can type this "jar cf myjar myapp" where "cf" - is a 
command to create and set name it, then goes a file name and after this a name of your apps folder.</li>
</ol>
<h4>To start with Google spreadsheets go through a small guide on the 
<a href="https://developers.google.com/sheets/api/quickstart/go">link</a></h4>
<p>After this save <b>client_secret.json</b> in the resources folder</p>
