@echo off
echo Starting CBT Scenario Editor Frontend...
echo.
echo Installing dependencies...
call npm install
echo.
echo Starting development server...
echo.
echo The application will be available at: http://localhost:3000
echo Backend API should be running at: http://localhost:8080
echo.
echo Default login credentials:
echo Email: admin@cbt.com
echo Password: Admin123!
echo.
call npm run dev
