To run the program, navigate to the project directory containing the source code. When in the directory, compile all the .java files using the command �javac *.java�, this will produce .class files. Start a new terminal window from the same directory and start up the server using the command �java TestServer�. Now, switch back to the other terminal window and start up the client program by running the command �java TestC�.

test or example files may already exist

A command line interface similar to the Linux command line for the client program. Once the client program is started, say, by you, the following commands should be supported:

login � this command takes one argument, your user ID. It is used by the application to determine which discussion groups you have subscribed to, and for each subscribed group, which posts you have read.

The user history information locally on the client machine using a file. This reduces the number of messages exchanged between the client and the server, and may allow the server to more efficiently support a large number of clients.

help � this command takes no argument. It prints a list of supported commands and sub-commands. For each command or sub-command, a brief description of its function and the syntax of usage are displayed.

Once a user is logged in, the following commands is supported:

ag - this command stands for �all groups�. It takes an optional argument, N, and lists the names of all existing discussion groups, N groups at a time, numbered 1 to N. If N is not specified, a default value is used. Below is an example output of the command �ag 5�. Whether or not a group is a subscribed group is indicated in parentheses. In this example, among the five groups displayed, the user is currently subscribed to groups comp.lang.python and comp.lang.javascript.
	1. ( ) comp.programming
	2. ( ) comp.os.threads
	3. ( ) comp.lang.c
	4. (s) comp.lang.python
	5. (s) comp.lang.javascript
The following sub-commands are supported:

s � subscribe to groups. It takes one or more numbers between 1 and N as arguments. E.g., given the output above, the user may enter �s 1 3� to subscribe to two more groups: comp.programming and comp.lang.c

u � unsubscribe. It has the same syntax as the s command, except that it is used to unsubscribe from one or more groups. E.g., the user can unsubscribe from group comp.lang.javascript by entering the command �u 5�

n � lists the next N discussion groups. If all groups are displayed, the program exits from the ag command mode

q � exits from the ag command, before finishing displaying all groups

sg - this command stands for �subscribed groups�. It takes an optional argument, N, and lists the names of all subscribed groups, N groups at a time, numbered 1 to N. If N is not specified, a default value is used. Below is an example output of the command �sg 5�. The number of new posts in each group is shown before the group. E.g., there are 18 new posts in group comp.programming since the user last listed this group, and there are no new posts in rec.arts.ascii
	1. 18 comp.programming
	2. 2 comp.lang.c
	3. 3 comp.lang.python
	4. 27 sci.crypt
	5. rec.arts.ascii

The same set of sub-commands as the ag command should be supported, except the s sub-command. These include the u, n, and q sub-commands.

rg - this command stands for �read group�. It takes one mandatory argument, gname, and an optional argument N, and displays the (status � new or not, time stamp, subject line) of all posts in the group gname, N posts at a time. If N is not specified, a default value is used. gname must be a subscribed group. When displaying posts, those unread (new) posts should be displayed first. Below is an example output of the command �rg comp.lang.python 5�

	1. N Nov 12 19:34:02 Sort a Python dictionary by value
	2. N Nov 11 08:11:34 How to print to stderr in Python?
	3. N Nov 10 22:05:47 �Print� and �Input� in one line
	4. Nov 9 13:59:05 How not to display the user inputs?
	5. Nov 9 12:46:10 Declaring custom exceptions
	
A list of 5 posts are displayed. Three new posts are shown first, indicated by the letter �N�. The following sub-commands are supported:

[id] � a number between 1 and N denoting the post within the list of N posts to display. The content of the specified post is shown. E.g., entering �1� displays the content of the post �Sort a Python dictionary by value�.

While displaying the content of a post, two sub-sub-commands are used:
	�n� � would display at most N more lines of the post content.
	�q� � would quit displaying the post content. The list of posts before opening the post is shown again with the post just opened marked as read.

r � marks a post as read. It takes a number or range of number as input. E.g., �r 1� marks the first displayed post to be read. �r 1-3� marks posts #1 to #3 in the displayed list to be read.

n � lists the next N posts. If all posts are displayed, the program exits from the rg command mode

p � post to the group. This sub-command allows a user to compose and submit a new post to