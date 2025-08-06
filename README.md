# life

<div>Implementation of <a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Conwoy's Game of Life</a></div>

<div>Build instructions:</div>
<ol>
  <li>Clone the project: <i>git clone https://github.com/sashonk/life</i></li>
  <li>Navigate to the directory</li>
  <li>Build the code by running: <i>mvn install</i></li>
  <li>Run the program: <i>java -jar target/app.jar</i></li>
</ol>

<div>The simulation starts instantly when the application is run. Artifacts are rendered on the JPanel. There is a set of predefined well-known figures (glyder, boat, triplet, etc.). The user can select a figure and place it on the field. The name of the selected figure is rendered in JFrame's title (as well as number of alive, dead, born and survived cells). These mouse buttons and keyboard keys are available for the user: </div>
<ul>
  <li>"1" - fast simulation</li>
    <li>"2" - slow simulation</li>
    <li>"0" - real time simulation</li>
    <li>LMB - zoom in (drag cursor from point A to point B while holding LMB)</li>
    <li>RMB - reset zoom</li>
    <li>MMB - place selected figure at cursor location</li>
    <li>"," - select previous figure</li>
    <li>"." - select next figure</li>
</ul>

<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/3df553de-7964-45f5-bd78-8a345465df0b" />
<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/96f4d0d8-d462-49ff-95ed-b2bcc1f4f665" />
<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/e28c7585-2e59-401c-9af2-7c8c0bb4b2b6" />
<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/59d90d05-c846-4a0a-bbfc-481722b03edd" />





