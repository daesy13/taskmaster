# LAB 26 - Beginning TaskMaster

## Daily Log
- [x] Main page has a heading at the top of the page.
- [x] An image to mock “my tasks” view.
- [x] Buttons at the bottom of the page to allow going to the “add tasks” and “all tasks” page.
- [x] On the “Add a Task” page, allows users to type in details about a new task, specifically a title and a body. 
- [x] When users click the “submit” button, it shows a “submitted!” label on the page.
- [x] All tasks page shows an image with a back button with no functionality for the moment.</br>

<img src="/screenshots/Screenshot_1581457330.png"
     width=150;  margin-right= 10px;/>
<img src="/screenshots/Screenshot_1581992542.png"
width=150;  margin-right= 10px;/>
<img src="/screenshots/Screenshot_1581992549.png"
width=150;  margin-right= 10px;/>
<img src="/screenshots/Screenshot_1581993139.png"
width=150;  margin-right= 10px;/></br></br></br>


# LAB 27 - Data in TaskMaster

## Daily Log
- [x] Create a Task Detail page. It should have a title at the top of the page, and a Lorem Ipsum description.
- [x] Create a Settings page. Where users enter their username and hit save.
- [x] The main front page should have three different buttons with hardcoded task titles. When a user taps one of the tasks, it goes to the Task Detail page, the task is displayed as a title at the top of the Detail page.
- [x] The homepage contains a button to visit the Settings page, and once the user has entered their username, it will display “{username}’s tasks” above the three task buttons.</br>

<img src="/screenshots/Screenshot_1582012418.png"
     width=150;  margin-right= 10px;/>
<img src="/screenshots/Screenshot_1582012427.png"
     width=150;  margin-right= 10px;/>
<img src="/screenshots/Screenshot_1582012433.png"
     width=150;  margin-right= 10px;/>
<img src="/screenshots/Screenshot_1582012438.png"
     width=150;  margin-right= 10px;/>
<img src="/screenshots/Screenshot_1582012443.png"
     width=150;  margin-right= 10px;/></br></br></br>

# LAB 28 - RecyclerView

## Daily Log
- [x] Create a Task class with a title, a body, and a state. 
- [x]  state could be “new”, “assigned”, “in progress”, or “complete”.

- [x] Refactor homepage to use a RecyclerView for displaying Task data. This should have hardcoded Task data for now.
- [x] When tap on any one of the Tasks in the RecyclerView, it should appropriately launch the detail page with the correct Task title displayed.
</br>

<img src="/screenshots/Screenshot_1582024395.png"
width=150;  margin-right= 10px;/>
<img src="/screenshots/Screenshot_1582025809.png"
width=150;  margin-right= 10px;/></br></br></br>

# LAB 29 - Room

## Daily Log

- [x] Set up Room in the application.
- [x] Modify Task class to be an Entity.
- [x] Modify the Add Task form to save the data entered in as a Task in your local database.
- [x] Refactor your homepage’s RecyclerView to display all Task entities in your database.
- [x] Ensure that the description and status of a tapped task are also displayed on the detail page.
</br>

<img src="/screenshots/Screenshot_1582506163.png"
width=150;  margin-right= 10px;/>
<img src="/screenshots/Screenshot_1582506167.png"
width=150;  margin-right= 10px;/></br></br></br>


# LAB 33 - Polish

## Daily Log
- [x] You can click the all Tasks button to visit the second activity
- [x] The tasks in the second page are clickable and trigger a toast
- [x] The toast is descriptive about the task that triggered the toast</br>

<img src="/screenshots/Screenshot_1582684858.png"
     width=150;  margin-right= 10px;/>
<img src="/screenshots/Screenshot_1582684862.png"
width=150;  margin-right= 10px;/></br></br></br>

# LAB 34 - Amplify and DynamoDB

## Daily Log

- [x] Created an AWS account and installed the Amplify CLI, and followed the Amplify set up.
- [x] Created a Task resource that replicates existing Task schema. 
- [x] Use AWS Amplify to access your data in DynamoDB instead of in Room.
- [x] Modified Task form to save the data entered in as a Task to DynamoDB.
- [x] Refactored homepage’s RecyclerView to display all Task entities in DynamoDB.
</br>

<img src="/screenshots/Screenshot_1583224125.png"
width=150;  margin-right= 10px;/>
<img src="/screenshots/Screen Shot 2020-03-03 at 12.28.42 AM.png"
width=300;  margin-right= 10px;/></br></br></br>