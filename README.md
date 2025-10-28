# MyLeague

**Authors:** Chris Burke, Evan Wilson, Jack Kerth, and Oladayo Ayorinde

## Project Summary

MyLeague is a Fantasy Football Draft application that allows users to view the current NFL offensive players and their stats, create custom leagues and teams, and manage their fantasy rosters. The application features player search and filtering capabilities, team management, and real-time player data from the NFL.

### Key Features
- View current NFL offensive players with detailed statistics
- Create multiple leagues with customizable roster configurations
- Build and manage teams within leagues
- Search players by name and filter by position or NFL team
- Add players to team rosters with position-specific slots (QB, RB, WR, TE, K, FLEX)
- View team rosters and remove players
- Player data fetched from Tank01 NFL Live Statistics API

## Build Instructions
1. **Clone the repository:**
```bash
   git clone https://github.com/bsu-cs222-fall25-dll/FinalProject-MyLeague.git
   cd FinalProject-MyLeague
```

2. **Set up API credentials:**
    - Locate the `.env.example` file at the root of the project
    - Replace `YOUR_API_KEY` so the second line reads: `API_KEY=your_actual_api_key`
    - Get your API key from [RapidAPI - Tank01 NFL Statistics](https://rapidapi.com/tank01/api/tank01-nfl-live-in-game-real-time-statistics-nfl)
    - Rename `.env.example` to `.env`

3. **Build and run the program:**
```bash
   ./gradlew run
```
- JDK 21 preferred
- Requires JavaFX 17 or higher

## Program Instructions

### Players View
When you launch the program, you'll start at the **Players View** where you can browse current NFL offensive players.

**Navigation Controls:**
- **Search Bar** (top): Search players by name
- **Position Filter** (top right): Filter by position (QB, RB, WR, TE, K)
- **Team Filter** (top right): Filter by NFL team
- **League Selector** (left sidebar): Dropdown labeled "Default" - select or create leagues
- **Team Selector** (left sidebar): Dropdown labeled "None" - select or create teams

**Creating Leagues:**
1. Select "Create" from the league selector dropdown
2. Enter your league name in the modal
3. Click "Add" to create the league

**Creating Teams:**
1. Select a league from the league selector
2. Select "Create" from the team selector dropdown
3. Enter your team name in the modal
4. Click "Add" to create the team

**Adding Players:**
1. Select a team from the team selector
2. Click the "Add" button on any player row
3. In the modal, click the button corresponding to the roster position (QB, RB, WR, TE, K, FLEX)
4. The player will be added to your team at that position

### Team View
Click the **"MyTeam"** button (left sidebar) to view your team's roster.

**Team View Features:**
- View all players on your selected team
- Search and filter your roster
- Each player displays their assigned roster position
- Click "Remove" to remove a player from your team
- Use the "Players" button to return to the Players View

**Note:** You cannot create new leagues or teams from the Team View page.

## Technology Stack
- **Language:** Java 21
- **UI Framework:** JavaFX 17
- **API:** Tank01 NFL Live Statistics API (RapidAPI)
- **Environment Management:** dotenv-java
- **Data Format:** JSON

---

## Version
Current release: **v0.1.0**