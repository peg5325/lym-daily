# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**임영웅 Daily** - A daily news aggregation service for fans of Korean singer Lim Young-woong, built as a personal portfolio project with a real end user (the developer's mother).

**Tech Stack (Planned):**
- Backend: Spring Boot 2.7, JPA, MySQL, Jsoup (crawling), OpenAI API (summarization), Spring Scheduler
- Frontend: Vue.js 3, Tailwind CSS, Axios
- Deployment: Docker, AWS EC2, Nginx

## Key Architecture Decisions

### Sub-Agent Architecture
The project uses a **sub-agent pattern** to separate concerns and maintain clean architecture. See AGENTS.md for detailed specifications.

**Backend Agents**:
- **NewsCollectorAgent**: Naver News API integration, deduplication
- **MediaCollectorAgent**: YouTube API integration, official channel prioritization
- **SummarizationAgent**: OpenAI integration for 40-char summaries and importance scoring
- **ContentCuratorAgent**: Ranking algorithm to select top 3 news (based on importance, source reliability, recency)
- **SchedulerAgent**: Daily 7 AM orchestration with retry logic
- **CacheAgent**: 1-hour caching for /api/today, permanent caching for AI summaries

**Frontend Agents (Composables)**:
- **DataFetchAgent**: API calls with loading/error states and auto-retry
- **DateNavigationAgent**: Date switching, Korean formatting, URL sync

### Data Collection Flow
1. SchedulerAgent triggers at 7 AM
2. NewsCollectorAgent → Naver API (10 articles)
3. MediaCollectorAgent → YouTube API (5 videos)
4. SummarizationAgent → OpenAI API (batch summarization)
5. ContentCuratorAgent → Selects top 3 by importance score
6. Store in MySQL, invalidate cache
7. Frontend fetches via CacheAgent-backed API

### Database Schema
Three core tables:
- `news`: Stores crawled news with AI-generated summaries
- `media`: YouTube videos/photos with thumbnails and view counts
- `schedule`: Manually entered event schedules (not crawled)

### API Endpoints (Planned)
- `GET /api/today` - Returns today's summary (top 3 news, media, weekly schedule)
- `GET /api/date/{date}` - Historical summaries
- Schedule CRUD endpoints for manual entry

## Development Workflow

### TODO.md Management
**CRITICAL**: Always check and update TODO.md when working on tasks. The file tracks:
- Week 1-4 development milestones
- API key setup requirements (Naver, YouTube, OpenAI)
- Deployment checklist
- Post-launch improvements

Update progress by checking off items as you complete them.

### Commit Convention
- `feat:` New features
- `fix:` Bug fixes
- `docs:` Documentation
- `style:` Code formatting
- `refactor:` Refactoring
- `test:` Test code
- `chore:` Build/config changes

### Branch Strategy & Pull Request Workflow
**CRITICAL**: Always create feature branches and push to GitHub for code review before merging.

**Workflow**:
1. Create feature branch: `git checkout -b feature/기능명`
2. Make changes and commit following commit conventions
3. Push to GitHub: `git push -u origin feature/기능명`
4. The user will review the code via GitHub Pull Request
5. Never merge directly to main or develop

**Branch Types**:
- `main`: Production deployment (protected)
- `develop`: Development branch (protected)
- `feature/기능명`: Feature branches (always create for new work)

**Example**:
```bash
# Starting new work on NewsCollectorAgent
git checkout -b feature/news-collector-agent
# ... make changes ...
git add .
git commit -m "feat: implement NewsCollectorAgent with Naver API integration"
git push -u origin feature/news-collector-agent
# User will create PR and review on GitHub
```

## Security Requirements

- Never commit API keys - use environment variables (.env)
- All external API keys must be configured via environment:
  - NAVER_API_KEY
  - YOUTUBE_API_KEY
  - OPENAI_API_KEY
- Ensure .env is in .gitignore

## AI Integration Specifics

### OpenAI Summarization Prompt
Target: 40-character Korean summaries focusing on "why this matters" rather than "what happened"
Keep summaries concise for elderly user (mother) to read quickly

### Content Curation
- Filter for importance, not just recency
- Prioritize official news and verified sources
- Mobile-first design - no scrolling needed on main screen

## User Context

**Primary User**: Developer's mother (elderly, Lim Young-woong fan)
**Key Requirements**:
- Extreme simplicity - no complex features
- Large, readable fonts
- Mobile-optimized (primary access device)
- Single-page design - all info visible without scrolling
- Daily updates ready by morning

**Success Metrics**:
- Reduce information gathering time from 30 minutes to 3 minutes
- Zero missed important news
- Daily usage by mother

## Development Phases

### MVP Scope (3-4 weeks)
**Week 1**: NewsCollectorAgent implementation, project setup, database schema
**Week 2**: SummarizationAgent, ContentCuratorAgent, SchedulerAgent, REST API
**Week 3**: MediaCollectorAgent, CacheAgent, frontend agents (composables), mobile optimization
**Week 4**: Docker deployment, AWS EC2 setup, user testing

**Agent Implementation Priority**:
1. NewsCollectorAgent (Week 1)
2. SummarizationAgent + ContentCuratorAgent (Week 2)
3. SchedulerAgent (Week 2)
4. MediaCollectorAgent + CacheAgent (Week 3)

See TODO.md for detailed task breakdown and AGENTS.md for agent specifications.

### Post-MVP Features (v2)
Only implement after user feedback:
- KakaoTalk notifications
- Video section
- Historical summaries (weekly/monthly)
- Dark mode
- Font size controls
- Bookmark functionality
