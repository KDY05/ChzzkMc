# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

- Build the plugin: `./gradlew build`
- Run test server: `./gradlew runServer`
- Clean build: `./gradlew clean build`

## Project Architecture

This is a Minecraft Paper plugin that integrates Chzzk (Korean streaming platform) chat with Minecraft servers. The plugin consists of three main components:

### Core Components

1. **ChzzkMc** (`src/main/java/io/github/kdy05/chzzkMc/ChzzkMc.java`): Main plugin class that initializes managers and handles plugin lifecycle
2. **ChatManager** (`src/main/java/io/github/kdy05/chzzkMc/core/ChatManager.java`): Manages Chzzk chat connection using chzzk4j library, handles chat events and filters vote commands
3. **VoteManager** (`src/main/java/io/github/kdy05/chzzkMc/core/VoteManager.java`): Implements 3-option voting system with boss bars for real-time vote display

### Key Features

- **Chat Integration**: Connects to Chzzk streaming chat and broadcasts messages to Minecraft server
- **Voting System**: 3-option voting triggered by chat commands (!투표1, !투표2, !투표3)
- **Boss Bar Display**: Shows real-time voting progress using colored boss bars
- **Vote Management**: Handles vote start/end, prevents duplicate votes per user, and displays results

### Dependencies

- Paper API 1.21
- chzzk4j library (0.1.1) for Chzzk API integration
- Fat JAR configuration includes chzzk4j in final build

### Configuration

The plugin uses `config.yml` with:
- `channel-id`: Chzzk channel ID for chat connection
- `broadcast-chat`: Toggle for chat broadcasting to Minecraft

### Command Structure

Main command: `/chzzkmc` (alias: `/cm`) with subcommands handled by ChzzkMcCommand class.