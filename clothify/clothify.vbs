' ==============================================================================
' Clothify Store - Silent Desktop Launcher
' Runs the application without opening a command prompt / console window.
' ==============================================================================
Set WshShell = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")

scriptDir = fso.GetParentFolderName(WScript.ScriptFullName)

jarFile = ""
If fso.FileExists(scriptDir & "\clothify-app.jar") Then
    jarFile = scriptDir & "\clothify-app.jar"
ElseIf fso.FileExists(scriptDir & "\target\clothify-1.0-SNAPSHOT-jar-with-dependencies.jar") Then
    jarFile = scriptDir & "\target\clothify-1.0-SNAPSHOT-jar-with-dependencies.jar"
ElseIf fso.FileExists(scriptDir & "\clothify-1.0-SNAPSHOT-jar-with-dependencies.jar") Then
    jarFile = scriptDir & "\clothify-1.0-SNAPSHOT-jar-with-dependencies.jar"
End If

If jarFile <> "" Then
    javaCmd = "javaw"
    If fso.FileExists(scriptDir & "\jre\bin\javaw.exe") Then
        javaCmd = """" & scriptDir & "\jre\bin\javaw.exe"""
    End If
    WshShell.CurrentDirectory = scriptDir
    WshShell.Run javaCmd & " -Xms256m -Xmx1024m -jar """ & jarFile & """", 0, False
Else
    MsgBox "Could not find Clothify executable JAR file." & vbCrLf & "Please run 'mvn clean package' first or check the installation folder.", vbCritical, "Clothify Store Launcher"
End If

Set WshShell = Nothing
Set fso = Nothing
