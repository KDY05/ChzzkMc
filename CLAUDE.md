# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

- Build the plugin: `./gradlew build`
- Run test server: `./gradlew runServer`
- Clean build: `./gradlew clean build`

## Project Architecture

This is a Minecraft Paper plugin that integrates Chzzk (Korean streaming platform) chat with Minecraft servers. The plugin uses **dependency injection** pattern and **modular architecture** for clean separation of concerns and high maintainability.

### Core Architecture

#### Main Components

1. **ChzzkMc** (`src/main/java/io/github/kdy05/chzzkMc/ChzzkMc.java`): Main plugin class that acts as dependency orchestrator, instantiating and wiring all components through constructor injection

2. **VoteManager** (`src/main/java/io/github/kdy05/chzzkMc/core/VoteManager.java`): Core voting controller that orchestrates voting flow using specialized managers

3. **ChatManager** (`src/main/java/io/github/kdy05/chzzkMc/core/ChatManager.java`): Handles Chzzk chat connection using chzzk4j library, processes chat events and filters vote commands

4. **ChzzkMcCommand** (`src/main/java/io/github/kdy05/chzzkMc/command/ChzzkMcCommand.java`): Command handler that reads configuration and passes parameters to managers

5. **ChzzkMcProvider** (`src/main/java/io/github/kdy05/chzzkMc/api/ChzzkMcProvider.java`): Simplified API provider for external plugin integration

#### Vote System Architecture (Modular Design)

The voting system is broken down into specialized managers for improved maintainability:

- **VoteBossBarManager** (`src/main/java/io/github/kdy05/chzzkMc/core/vote/VoteBossBarManager.java`): Handles boss bar creation, updates, and player management
- **VoteTimerManager** (`src/main/java/io/github/kdy05/chzzkMc/core/vote/VoteTimerManager.java`): Manages vote timers and action bar countdown displays  
- **VoteResultManager** (`src/main/java/io/github/kdy05/chzzkMc/core/vote/VoteResultManager.java`): Handles result announcements and event firing

### Dependency Injection Implementation

- **Constructor Injection**: All components receive dependencies through constructors
- **Manual DI**: No external DI framework used, dependencies managed by main plugin class
- **Modular Composition**: VoteManager composes specialized managers for different responsibilities
- **Simplified API**: ChzzkMcProvider only depends on VoteManager, reducing coupling

### Key Features

- **Flexible Voting System**: Configurable 2-4 vote options with custom titles
- **Unified Vote Logic**: Single vote method serves both command and API triggers
- **Real-time Display**: Colored boss bars with live progress updates and action bar countdown  
- **User Management**: Prevents duplicate votes per user, allows vote changes
- **Modular Architecture**: Specialized managers for boss bars, timers, and results
- **API Integration**: External plugin access through simplified ChzzkMcProvider
- **Chat Broadcasting**: Optional Chzzk chat relay to Minecraft server with anonymous user support

### Dependencies

- Paper API 1.21.5-R0.1-SNAPSHOT (Minecraft server platform)
- chzzk4j library (0.1.1) for Chzzk streaming platform integration
- Java 21 target version
- Fat JAR configuration includes chzzk4j in final build

### Configuration

The plugin uses `config.yml` with:
- `channel-id`: Chzzk channel ID for chat connection
- `broadcast-chat`: Toggle for chat broadcasting to Minecraft
- `vote.option`: Number of vote options (2-4, default: 3)
- `vote.titles`: Array of custom vote option titles  
- `vote.durationSec`: Vote duration in seconds (default: 120)

### Command Structure

Main command: `/chzzkmc` (alias: `/cm`) with permission `chzzkmc.use`:
- `help`: Display command usage information
- `reload`: Reload configuration and reconnect to Chzzk chat
- `vote start`: Start a vote using configuration settings
- `vote end`: Manually end the currently active vote

### API Structure

External plugins can integrate through:
- `ChzzkMcProvider.getInstance()`: Get singleton provider instance
- `startVote(String[] titles, int duration)`: Start programmatic vote with custom options
- `isVoteActive()`: Check if vote is currently running
- `endVote()`: Force end current vote
- `VoteEndEvent`: Bukkit event fired when API-initiated votes complete, containing results and statistics

### Performance Optimizations

#### Asynchronous Vote Processing
- **ChatManager**: Vote command processing (`processVoteCommand`) runs asynchronously using `runTaskAsynchronously()` to prevent blocking the main thread
- **VoteManager**: Uses thread-safe `AtomicIntegerArray` for vote counts and `synchronized` blocks for user vote management
- **Boss Bar Updates**: Separated from vote processing - updates every 1 second via scheduled task instead of immediate updates
- **Chat Broadcasting**: Remains synchronous (`runTask`) to maintain message ordering and proper UI updates

#### Thread Safety Implementation
- `volatile boolean voteActive`: Ensures visibility across threads
- `AtomicIntegerArray voteCounts`: Thread-safe atomic operations for vote counting
- `synchronized (userVotes)`: Protects HashMap operations during concurrent vote processing
- `ConcurrentHashMap` in ChatBroadcastManager: Thread-safe user color caching

### Architecture Benefits

- **High Maintainability**: Each manager handles a specific responsibility
- **Easy Testing**: Components can be tested independently  
- **Code Reusability**: Common vote logic shared between API and commands
- **Clean Separation**: UI (boss bars), logic (voting), and timing concerns are separated
- **Reduced Complexity**: VoteManager went from 322 to 143 lines (55% reduction)
- **Improved Performance**: Asynchronous vote processing prevents main thread blocking during high chat activity