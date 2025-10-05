@echo off
echo ========================================
echo   Subindo Midas Fintech para GitHub
echo ========================================
echo.

cd /d "C:\Users\rugge_p2gkz2r\Desktop\midas-ai\midas-fintech-java"

echo [1/5] Verificando status do repositorio...
git status
echo.

echo [2/5] Adicionando todos os arquivos...
git add .
echo.

echo [3/5] Criando commit com as alteracoes...
git commit -m "Sprint 1 Completa: API REST Nivel 1, Documentacao e Testes"
echo.

echo [4/5] Verificando remote...
git remote -v
echo.

echo [5/5] Enviando para GitHub (branch main)...
git push origin main
echo.

echo ========================================
echo   Concluido! Verifique em:
echo   https://github.com/viniruggeri/midas-fintech-java
echo ========================================
pause

