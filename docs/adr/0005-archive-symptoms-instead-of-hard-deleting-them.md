# Archive symptoms instead of hard-deleting them

Users can stop tracking a symptom by archiving it, but the product must preserve its history and reactivate the same symptom row if the user starts tracking it again. The MVP intentionally has no per-symptom hard delete because deleting a symptom would damage historical logs and trend interpretation.

## Consequences

- Active symptom pickers hide archived symptoms.
- Reactivation restores the same symptom identity instead of creating a duplicate.
- Duplicate-name checks must account for trim, Unicode normalization, and case-insensitive comparison.
