CREATE PROCEDURE GetVacationDuration
    @login nchar(36),
    @date date
AS
BEGIN
    DECLARE @Cal_Id int;
SELECT @Cal_Id = Cal_Id FROM Users WHERE login = @login;
IF @Cal_Id IS NULL
BEGIN
        PRINT 'User not found or calendar not assigned';
        RETURN;
END

    DECLARE @DTS date, @DTE date, @NextDTS date;
SELECT TOP 1 @DTS = DTS, @DTE = DTE
FROM Holidays
WHERE login = @login AND DTS <= @date AND DTE >= @date
ORDER BY DTS;

IF @DTS IS NULL
BEGIN
        PRINT 'No vacation on this date';
        RETURN;
END

    SET @NextDTS = @DTE;
    WHILE 1=1
BEGIN
        DECLARE @NextDTE date;
SELECT TOP 1 @NextDTE = DTE
FROM Holidays
WHERE login = @login AND DTS = DATEADD(day, 1, @NextDTS);

IF @NextDTE IS NULL OR NOT EXISTS (
            SELECT 1 FROM Calendar
            WHERE Cal_Id = @Cal_Id AND
                  C_YEAR = YEAR(DATEADD(day, 1, @NextDTS)) AND
                  C_MONTH = MONTH(DATEADD(day, 1, @NextDTS)) AND
                  SUBSTRING(C_DAYS, DAY(DATEADD(day, 1, @NextDTS)), 1) = 'Y'
        )
        BREAK;

        SET @NextDTS = @NextDTE;
        SET @DTE = @NextDTE;
END

    DECLARE @totalDays int = 0;
    DECLARE @currentDate date = @DTS;

    DECLARE @calendarData TABLE (C_MONTH int, C_DAYS nchar(93));
INSERT INTO @calendarData
SELECT C_MONTH, C_DAYS
FROM Calendar
WHERE Cal_Id = @Cal_Id AND C_YEAR = YEAR(@currentDate);

WHILE @currentDate <= @DTE
BEGIN
        DECLARE @c_days nchar(93);
SELECT @c_days = C_DAYS
FROM @calendarData
WHERE C_MONTH = MONTH(@currentDate);

IF SUBSTRING(@c_days, DAY(@currentDate), 1) = 'Y'
            SET @totalDays = @totalDays + 1;

        SET @currentDate = DATEADD(day, 1, @currentDate);
END

    PRINT 'Total working days in vacation: ' + CAST(@totalDays AS nvarchar(10));
END