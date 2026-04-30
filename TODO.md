# Godcore Mod - TODO List

**Creator:** Waya Steurbaut  
**YouTube:** https://www.youtube.com/@wayacreate  
**Version:** 1.0 MVP  
**Platform:** Minecraft Java 1.21.1 + NeoForge

---

## Phase 1 - MVP (Launch)

### Core Gameplay Loop
- [ ] Player joins world → activates Godcore → talks to AI → AI responds → AI acts in world → player approves/reverts changes → relationship evolves

### A. AI Chat System
- [x] Implement `/god activate` command
- [x] Implement `/god chat <message>` command
- [x] Implement `/god summon` command
- [x] Implement `/god sleep` command
- [x] Create chat UI window
- [x] Add context memory per world
- [x] Add per-player chat history (optional)
- [x] Implement personality responses

### B. NPC Physical Body
- [x] Create Godcore entity (humanoid)
- [x] Implement walking behavior
- [x] Implement head turning
- [x] Implement looking at player
- [x] Implement mining animation
- [x] Implement tool swinging
- [x] Implement emotes
- [x] Implement sitting/meditating
- [x] Add GeckoLib animations (optional)

### C. AI World Actions
- [x] Implement block placing
- [x] Implement block breaking
- [x] Implement simple structure building
- [x] Implement mob fighting
- [x] Implement ore mining
- [x] Implement chest organizing
- [x] Implement following players
- [x] Implement area patrol
- [x] Implement particle/effect casting
- [x] Implement command execution (permission-based)

### D. Approval System
- [x] Create approval UI for risky actions
- [x] Implement [Accept] [Deny] [Always Allow This Type] buttons
- [x] Track action types for "always allow"

### E. Undo / Revert System
- [x] Implement `/god undo last` command
- [x] Implement `/god undo 10m` command
- [x] Implement `/god rollback area` command
- [x] Track all AI changes
- [x] Create backup snapshots

### F. Personality System
- [x] Implement Wise Oracle personality
- [x] Implement Friendly Builder personality
- [x] Implement Guardian Knight personality
- [x] Implement Chaotic Trickster personality
- [x] Implement Cold Machine personality
- [x] Implement Storyteller personality
- [x] Implement Waya Mode personality
- [x] Implement `/god personality <type>` command
- [ ] Add custom prompt support (`/god prompt edit`)

### G. Permissions System
- [x] Define Player permission tier (ask questions, request help, simple builds)
- [x] Define Moderator permission tier (inspect grief, patrol areas, mute AI)
- [x] Define Admin permission tier (run scripts, world edits, config AI, memory wipe)
- [x] Implement permission checks

### H. Safety Systems
- [ ] Block OP commands without admin approval
- [ ] Block lava/grief edits by default
- [ ] Block chunk deletion
- [ ] Block self-updating code without consent
- [ ] Implement rate limiting for actions
- [x] Implement backup snapshots
- [x] Add emergency commands (`/god freeze`, `/god disable`, `/god rollback all`, `/god memory clear`)

### I. Technical Architecture
- [x] Create package structure:
  - [x] `com.godcore.command`
  - [x] `com.godcore.ai`
  - [x] `com.godcore.entity`
  - [x] `com.godcore.gui`
  - [x] `com.godcore.worldactions`
  - [x] `com.godcore.memory`
  - [x] `com.godcore.permissions`
  - [x] `com.godcore.rollback`
  - [ ] `com.godcore.integrations`
  - [x] `com.godcore.network`

---

## Phase 2

### A. Voice Interaction
- [ ] Integrate Simple Voice Chat
- [ ] Implement push-to-talk to Godcore
- [ ] Implement speech-to-text bridge
- [ ] Implement AI text response
- [ ] Add optional TTS voice reply
- [ ] Implement `/god voice on` command
- [ ] Implement `/god voice off` command

### B. Memory System
- [ ] Implement multiuser memory
- [ ] Remember player names
- [ ] Remember past conversations
- [ ] Remember rivalries/friendships
- [ ] Remember server lore
- [ ] Remember projects

### C. Better Movement
- [ ] Improve pathfinding
- [ ] Add smoother animations
- [ ] Implement better navigation

---

## Phase 3

### A. Image to Item Generator
- [ ] Implement `/god create item from <url>` command
- [ ] Create texture JSON generation
- [ ] Create item registration
- [ ] Create behavior template

### B. Dynamic GUI Builder
- [ ] Create settings menu
- [ ] Create approvals menu
- [ ] Create memory viewer
- [ ] Create tasks menu
- [ ] Create personality editor
- [ ] Use LDLib2 or custom GUI system

### C. Script / Datapack Generator
- [ ] Generate mcfunctions
- [ ] Generate loot tables
- [ ] Generate datapacks
- [ ] Generate JSON recipes
- [ ] Generate advancements
- [ ] Add approval before loading

### D. Java Code Generation (Controlled)
- [ ] Implement custom item abilities generation
- [ ] Implement mob AI behavior changes
- [ ] Implement block interactions
- [ ] Implement command generation
- [ ] Implement particles/effects systems
- [ ] Implement custom events
- [ ] Implement utility mechanics
- [ ] Add safety rules (no unrestricted execution, sandboxed modules, validate imports)
- [ ] Implement `/god code make <description>` command
- [ ] Implement `/god code review pending` command
- [ ] Implement `/god code apply` command

### E. LDLib2 GUI / GUI Mutation System
- [ ] Implement inventory menu generation
- [ ] Implement skill tree generation
- [ ] Implement quest screen generation
- [ ] Implement admin panel generation
- [ ] Implement shop generation
- [ ] Implement confirmation dialogs
- [ ] Implement animated HUD overlays
- [ ] Implement storage search UI
- [ ] Implement Godcore control panel
- [ ] Add GUI modification (add buttons, reorder layout, recolor theme, add tabs)
- [ ] Implement `/god gui make <type>` command
- [ ] Implement `/god gui improve <target>` command
- [ ] Implement `/god gui revert last` command

### F. Custom Mobs
- [ ] Implement dynamic quest generation
- [ ] Implement custom mob creation

---

## AI Backend

### Cloud Option
- [x] Integrate Google Gemini API
- [x] Add API key configuration GUI
- [x] Add API key verification
- [x] Add connection testing
- [x] Use free models (gemini-1.5-flash)

### Local Option
- [ ] Add Ollama support
- [ ] Add local LLM bridge support

### Bridge App
- [x] Create external companion app structure:
  - [x] `bridge/main.py` (WebSocket server)
  - [x] `bridge/requirements.txt` (Python dependencies)
  - [ ] `bridge/api/`
  - [ ] `bridge/prompts/`
  - [ ] `bridge/tools/`
  - [ ] `bridge/memory/`
  - [ ] `bridge/speech/`
- [ ] Implement API calls
- [ ] Implement STT/TTS
- [ ] Implement memory database
- [ ] Implement moderation
- [ ] Implement tool execution
- [ ] Implement websocket communication with mod

---

## Dependencies

### Current Status
- [x] NeoForge 1.21.1
- [x] Java 21
- [x] Gradle 8.8
- [x] GeckoLib (animations) - v4.7
- [x] LDLib2 (GUI) - v2.2.0 via Modrinth
- [ ] Simple Voice Chat - dependency format issues
- [ ] Baritone - dependency format issues

### Workarounds
- [ ] Use vanilla GUI instead of LDLib2 for now
- [ ] Implement basic pathfinding instead of Baritone
- [ ] Implement voice without Simple Voice Chat dependency

---

## Testing Checklist

### Core Features
- [ ] Test `/god activate` command
- [ ] Test `/god chat <message>` command
- [ ] Test `/god summon` command
- [ ] Test entity spawning and movement
- [ ] Test personality switching
- [ ] Test approval system
- [ ] Test undo/rollback commands
- [ ] Test permission tiers

### Advanced Features
- [ ] Test voice interaction
- [ ] Test memory system
- [ ] Test GUI generation
- [ ] Test code generation
- [ ] Test item generation
- [ ] Test structure building

### Safety
- [ ] Test emergency commands
- [ ] Test rate limiting
- [ ] Test permission checks
- [ ] Test backup/restore

---

## Known Issues

- Simple Voice Chat dependency format incompatible with current setup
- Baritone dependency format incompatible with current setup
- GUI implementation uses vanilla Minecraft components (LDLib2 integration pending)
- Bridge app implementation incomplete

---

## Next Priority

1. **Get basic build working** - Core commands, entity spawning, chat system
2. **Implement approval UI** - Make AI actions safe
3. **Add undo/rollback** - Make AI actions reversible
4. **Implement personalities** - Make AI interesting
5. **Add memory system** - Make AI remember
6. **Add voice integration** - Make AI conversational
7. **Add GUI system** - Make AI controllable
8. **Add code generation** - Make AI creative

---

## Success Metrics

### MVP Goals
- [ ] 1,000 downloads
- [ ] 80% crash-free sessions
- [ ] Positive community feedback
- [ ] Viral showcase potential

### Growth Goals
- [ ] 10K+ downloads
- [ ] SMP adoption
- [ ] YouTube coverage
- [ ] Community plugins/packs
