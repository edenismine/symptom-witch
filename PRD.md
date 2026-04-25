# Symptom Witch Product Requirements Document (MVP)

## Problem Statement

People managing recurring symptoms often fail to track consistently because current tools are too heavy, too slow, or not trusted for privacy. Without dependable tracking, users cannot clearly identify patterns over time or communicate changes effectively in care conversations.

## Product Vision

Symptom Witch helps people track symptoms quickly, privately, and consistently in everyday life, including low-connectivity moments.

## Product Goals

1. Make symptom logging fast enough to complete in seconds, even on difficult days.
2. Help users spot meaningful symptom patterns through clear history and simple trends.
3. Build trust through privacy-first data handling and reliable capture/sync behavior.
4. Prove repeat usage and habit formation with a focused MVP scope.

## Non-Goals (MVP)

- Clinical diagnosis, treatment recommendations, or medical advice.
- Full clinician workflow and EHR integration.
- Broad wellness platform expansion beyond symptom tracking.
- Advanced analytics/reporting beyond lightweight trend visibility.

## Target Users

- Primary: adults managing recurring or chronic symptoms.
- Secondary: users with intermittent connectivity who still need dependable logging.
- Influencer segment: users preparing structured symptom context for provider visits.

## Core User Journeys

1. **Set up tracking quickly**
  A new user selects what to track (including custom symptoms) and reaches first value in the first session.
2. **Log in the moment**
  A user records symptom intensity and optional context in a few steps with minimal cognitive load.
3. **Review and reflect**
  A user views history and trend summaries for a chosen window to understand how symptoms change over time.
4. **Adjust tracking as needs change**
  A user can add, rename, and stop tracking symptoms while preserving confidence in past records.
5. **Use comfortably every day**
  A user can personalize appearance/interactions and continue core actions when offline.

## Prioritized Capabilities

1. Fast symptom logging.
2. Personal symptom setup and management.
3. History and trend visibility.
4. Preferences and personalization.
5. Reliable offline capture with clear sync status.
6. Account trust basics, including account deletion.

## UX and Accessibility Requirements

- Mobile-first, low-friction flows that work in short sessions.
- Clear confirmation and actionable error recovery in all core actions.
- Accessibility baseline for keyboard/assistive tech support, readable contrast, and scalable text.
- Information is never conveyed by color alone.
- Touch targets and spacing support reduced dexterity and fatigue scenarios.
- Offline/pending/synced states are explicit and understandable.

## Data, Privacy, and Trust Requirements

- Data minimization by default: collect only what is required for core product value.
- Symptom logs and preferences are treated as sensitive health-adjacent data.
- Strict user data boundaries: users can only access and modify their own records.
- Clear account deletion and data export controls.
- Defined retention policy for active data, deleted data, backups, and audit logs.
- Non-production environments must not rely on unsafe real-user data practices.

## Reliability and Performance Requirements

- Core user experience target: at least 99.5% monthly availability.
- Typical core interactions should feel near-instant under normal network conditions.
- No acknowledged-write data loss for symptom logs.
- Logging remains usable during temporary connectivity loss.
- Reconnection behavior is automatic, transparent, and trustworthy.

## Data Quality and Auditability Requirements

- Core records require complete, valid inputs at write time.
- Invalid or inconsistent states are rejected with clear user-facing feedback.
- Time handling is consistent to prevent distorted history/trend interpretation.
- High-risk actions (for example, account deletion and privileged operations) are auditable.
- Basic quality monitoring covers write failures, sync failures, and integrity anomalies.

## Success Metrics

- Activation: users who complete setup and first log in initial session/day.
- Habit formation: users logging on at least 3 distinct days in first 14 days.
- Engagement depth: average logs per active user per week.
- Retention: D7 and D30 retention after first completed log.
- Insight usage: active users viewing history/trends weekly.
- Reliability trust: successful offline-to-online reconciliation rate.
- User confidence: qualitative sentiment on speed, clarity, and privacy trust.

## Risks and Assumptions

### Assumptions

- Simplicity and speed drive early adoption more than advanced analytics.
- Lightweight trend views are sufficient for MVP perceived value.
- Privacy and reliability are major drivers of continued use.

### Risks

- Tracking fatigue reduces sustained engagement after onboarding.
- Perceived privacy concerns suppress activation and retention.
- Offline sync confusion or failures erode trust quickly.
- Weak data quality controls degrade trend usefulness.

## Release Approach and Go/No-Go Criteria

### Rollout

- Start with a limited early cohort.
- Monitor activation, repeat logging, and reliability signals.
- Expand only after core trust and usage thresholds stabilize.

### Go/No-Go Criteria

- Core journey works reliably: setup -> first log -> repeat logs -> review.
- Early cohort meets minimum activation and repeat-usage targets.
- Reliability and sync success meet agreed thresholds.
- User feedback confirms the experience is fast, clear, and trustworthy.