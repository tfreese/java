1. uv package manager installieren:
winget install --id=astral-sh.uv -e

2. verify installation:
uv --version

3. use proxy settings for vw environment:
set UV_SYSTEM_CERTS=true

4. install spec kit:
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@v0.13.1

5. update sys path:
uv tool update-shell

6. move to target folder
cd c:\incubator\workspace\

7. start project
specify version
specify check

// Deprecated: Aufrufe mit Punkt /speckit.constitution
//specify init addressbook --integration copilot

// Mit --skills sind die Aufrufe mit Bindestrich /speckit.constitution -> /speckit-constitution
specify init spec-kit-addressbook --integration copilot --integration-options "--skills"


 1. Go to the project folder:
 2. Start using slash commands with your coding agent:                                                                          
     2.1 /speckit-constitution - Establish project principles
     2.2 /speckit-specify - Create baseline specification
   	   ○ /speckit-clarify (optional) - Ask structured questions to de-risk ambiguous areas before planning (run before /speckit.plan if used)
     2.3 /speckit-plan - Create implementation plan (Auch nach manueller anpassung der Spezifikation)
       ○ /speckit-checklist (optional) - Generate quality checklists to validate requirements completeness, clarity, and consistency (after /speckit-plan)
     2.4 /speckit-tasks - Generate actionable tasks
       ○ /speckit-analyze (optional) - Cross-artifact consistency & alignment report (after /speckit-tasks, before /speckit-implement)
     2.5 /speckit-implement - Execute implementation
     2.6 /speckit-converge - Assess the codebase and append remaining work as tasks

#########################################################################################################################################################	 

Basisumgebung definieren.
/speckit-constitution #/inputs/01-constitution.md

Erzeugt eine neue Anforderung.
/speckit-specify #/inputs/02-specify.md

Kläre Unklarheiten.
/speckit-clarify #/inputs/03-clarify.md

/speckit-plan #/inputs/04-plan.md
/speckit-checklist #/inputs/05-checklist.md
/speckit-tasks specs/002-person-management #/inputs/06-tasks.md
/speckit-analyze specs/002-person-management

###################################################################################################

/speckit.implement specs/002-person-management T001

.... oder direkt mehrere 1, 2, 3, 4 - 10
/speckit.implement specs/002-person-management T001 T002 T003 T004 bis T010
