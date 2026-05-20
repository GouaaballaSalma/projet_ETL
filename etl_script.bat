@echo off
TITLE Workflow ETL LCN
color 0B


:: ==========================================================
set PDI_PATH=C:\pdi-ce-11.0.0.0-237\data-integration

set JOB_FILE=%~dp0pentaho\jobs\JOB_LCN_ETL_PRINCIPAL.kjb
:: ==========================================================

echo ==========================================================
echo        Workflow ETL LCN
echo ==========================================================
echo Date : %date% %time%
echo.

if not exist "%PDI_PATH%\Kitchen.bat" (
    echo [ERREUR] Le dossier Pentaho est introuvable a l'emplacement :
    echo %PDI_PATH%
    echo Veuillez verifier le chemin.
    pause
    exit
)

echo Veuillez saisir les informations pour le traitement :
echo.
set /p DATE_ARR="Date d'arrete (ex: 20260406) : "
set /p NUM_LOT="Numero du lot (ex: 001) : "
set /p CHEMIN_FICHIER="Chemin complet du fichier source (Glissez-deposez le fichier ici) : "
echo.

echo ==========================================================
echo [EN COURS] Lancement de l'ETL avec Kitchen...
echo Veuillez patienter pendant le traitement des donnees LCN...
echo ==========================================================
echo.

cd /d "%PDI_PATH%"

:: Lancement du Job avec passage des parametres interactifs
call Kitchen.bat /file:"%JOB_FILE%" "/param:LOT_VAL=%NUM_LOT%" "/param:FICHIER_ENTREE=%CHEMIN_FICHIER%" "/param:DATE_ARRETE_VAL=%DATE_ARR%" /level:Basic
echo.
echo ==========================================================
:: Gestion du statut de retour
if %errorlevel% neq 0 (
    color 0C
    echo [ERREUR] Le workflow s'est arrete avec des erreurs.
    echo Veuillez verifier les messages logs ci-dessus.
) else (
    color 0A
    echo [SUCCES] Le traitement ETL a ete execute avec succes !
    echo Les donnees de la plateforme LCN ont ete mises a jour.
)
echo ==========================================================
echo.

pause