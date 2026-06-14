# Technical Planning Question Guide

Ask questions only when the answer materially changes implementation.

## Blocking Questions

Ask before finalizing the plan if unclear:

- Which phase should be planned first?
- Is the existing AI context decision still valid?
- Can a required dependency be added?
- Should API-first use manual contract implementation or code generation?
- Which authentication mechanism is expected?
- Should optional scope be included now or left for later?
- Should a build or runtime configuration change be planned?

## Non-Blocking Questions

Do not block the plan for minor details. Mark assumptions instead:

- exact package names if base package can be detected;
- exact class names if conventional names are obvious;
- detailed README wording;
- optional polish tasks;
- minor validation message wording.

## Approval Questions

Always ask before:

- replacing a project decision;
- adding dependencies not already approved;
- moving optional scope into mandatory scope;
- changing the phase order substantially;
- updating `.aicontext/memory/project-decisions.md` with a new decision.
