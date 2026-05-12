# Symptom Witch

Symptom Witch helps people track recurring symptoms quickly, privately, and consistently, including moments with poor connectivity. The product context is the personal tracking loop: choose what to track, log symptoms in the moment, review patterns, and keep trust through clear data boundaries.

## Language

**User**:
A person using Symptom Witch to manage recurring or chronic symptoms.
_Avoid_: Patient, customer, account

**Auth0 User**:
The external identity record managed by Auth0 for a **User**.
_Avoid_: App user, profile

**App User**:
The Symptom Witch-owned record that links a **User** to an **Auth0 User** by Auth0 subject.
_Avoid_: Auth0 user, account

**Symptom**:
A user-defined thing the **User** wants to monitor over time.
_Avoid_: Condition, diagnosis

**Active Symptom**:
A **Symptom** that is currently available for new **Symptom Logs**.
_Avoid_: Enabled symptom

**Archived Symptom**:
A **Symptom** the **User** has stopped tracking for new logs while preserving its history.
_Avoid_: Deleted symptom

**Symptom Log**:
A point-in-time record of a **Symptom** occurrence with intensity, time, and optional context.
_Avoid_: Entry, event

**Intensity**:
The **User**'s 1-5 rating for a **Symptom Log**.
_Avoid_: Severity, score

**Occurred At**:
The UTC timestamp for when the symptom experience happened.
_Avoid_: Created at, logged at

**Note**:
Optional free text attached to a **Symptom Log** for context.
_Avoid_: Payload, comment

**History**:
The ordered view of past **Symptom Logs** for a selected **Symptom**.
_Avoid_: Feed, audit log

**Trend**:
A lightweight summary of how a selected **Symptom** changes over a fixed time window.
_Avoid_: Diagnosis, report

**Rolling Mean**:
A subtle trend line showing the 7-day average intensity when enough **Symptom Logs** exist.
_Avoid_: Prediction, recommendation

**Pending Mutation**:
A locally saved user action waiting to sync to the API.
_Avoid_: Draft, unsaved change

**Sync State**:
The user-visible status of whether local work is pending, synced, or failed.
_Avoid_: Network status

**Preference**:
A user-chosen setting that changes the experience without changing symptom history.
_Avoid_: Configuration

**Account Deletion**:
The irreversible flow that removes the **User**'s product data and Auth0 identity after typed confirmation.
_Avoid_: Deactivation, archive

## Relationships

- A **User** has one **Auth0 User** and one **App User**.
- A **User** owns zero or more **Symptoms**.
- A **Symptom** is either an **Active Symptom** or an **Archived Symptom**.
- An **Archived Symptom** keeps its historical **Symptom Logs** and can be reactivated as the same **Symptom**.
- A **Symptom Log** belongs to exactly one **Symptom** and exactly one **User**.
- **History** and **Trends** are derived from **Symptom Logs** for a selected **Symptom**.
- A **Pending Mutation** eventually becomes synced, remains pending, or enters an error **Sync State**.
- **Account Deletion** removes user-owned product data, including **Symptoms**, **Symptom Logs**, and **Preferences**.

## Example dialogue

> **Dev:** "If a user stops tracking Headache, should we delete the symptom?"
> **Domain expert:** "No. Headache becomes an **Archived Symptom** so old **Symptom Logs** still appear in **History** and **Trends**."
> **Dev:** "If they start tracking Headache again later, do we create a new symptom?"
> **Domain expert:** "No. Reactivate the same **Symptom** so history stays continuous."
> **Dev:** "When logging offline, is the log saved only after the API responds?"
> **Domain expert:** "No. The action becomes a **Pending Mutation** immediately and the **Sync State** tells the user when it is synced or needs attention."

## Flagged ambiguities

- "User" and "Auth0 user" are distinct: **User** is the person using the product; **Auth0 User** is the external identity record.
- "Symptom" and "Symptom Log" are distinct: **Symptom** is what is tracked; **Symptom Log** is one recorded occurrence.
- "Archive" is the canonical term for stopping symptom tracking while preserving history; do not call this per-symptom deletion.
- "Occurred at" and "created at" are distinct: **Occurred At** captures when the symptom happened; creation/update timestamps describe record lifecycle.
