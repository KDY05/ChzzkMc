# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

- Build the plugin: `./gradlew build`
- Run test server: `./gradlew runServer`
- Clean build: `./gradlew clean build`

## Project Architecture

This is a Minecraft Paper plugin that integrates Chzzk (Korean streaming platform) chat with Minecraft servers. The plugin uses **dependency injection** pattern for clean component separation and testability.

### Core Components

1. **ChzzkMc** (`src/main/java/io/github/kdy05/chzzkMc/ChzzkMc.java`): Main plugin class that acts as dependency orchestrator, instantiating and wiring all components through constructor injection
2. **VoteManager** (`src/main/java/io/github/kdy05/chzzkMc/core/VoteManager.java`): Manages flexible voting system (2-4 options) with boss bar displays, supports both chat-triggered and API-triggered votes
3. **ChatManager** (`src/main/java/io/github/kdy05/chzzkMc/core/ChatManager.java`): Handles Chzzk chat connection using chzzk4j library, processes chat events and filters vote commands
4. **ChzzkMcCommand** (`src/main/java/io/github/kdy05/chzzkMc/command/ChzzkMcCommand.java`): Command handler with injected dependencies for plugin, vote, and chat managers
5. **ChzzkMcProvider** (`src/main/java/io/github/kdy05/chzzkMc/api/ChzzkMcProvider.java`): Singleton API provider for external plugin integration

### Dependency Injection Implementation

- **Constructor Injection**: All components receive dependencies through constructors
- **Manual DI**: No external DI framework used, dependencies managed by main plugin class
- **Loose Coupling**: Components interact through interfaces rather than direct static references

### Key Features

- **Flexible Voting System**: Configurable 2-4 vote options with custom titles
- **Dual Vote Triggers**: Chat commands (!투표1, !투표2, etc.) and programmatic API calls
- **Real-time Display**: Colored boss bars with live progress updates and action bar countdown
- **User Management**: Prevents duplicate votes per user, allows vote changes
- **API Integration**: External plugin access through ChzzkMcProvider with VoteEndEvent
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
- `vote start`: Manually start a vote using config settings
- `vote end`: Manually end the currently active vote

### API Structure

External plugins can integrate through:
- `ChzzkMcProvider.getInstance()`: Get singleton provider instance
- `startVote(String[] titles, int duration)`: Start programmatic vote with custom options
- `isVoteActive()`: Check if vote is currently running
- `endVote()`: Force end current vote
- `VoteEndEvent`: Bukkit event fired when API-initiated votes complete, containing results and statistics