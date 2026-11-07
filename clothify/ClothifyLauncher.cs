using System;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;

namespace Clothify
{
    static class Program
    {
        [STAThread]
        static void Main(string[] args)
        {
            try
            {
                string exeDir = AppDomain.CurrentDomain.BaseDirectory;
                string jarPath = Path.Combine(exeDir, "clothify-app.jar");

                if (!File.Exists(jarPath))
                {
                    jarPath = Path.Combine(exeDir, "target", "clothify-1.0-SNAPSHOT-jar-with-dependencies.jar");
                }
                if (!File.Exists(jarPath))
                {
                    jarPath = Path.Combine(exeDir, "clothify-1.0-SNAPSHOT-jar-with-dependencies.jar");
                }

                if (!File.Exists(jarPath))
                {
                    MessageBox.Show("Could not find 'clothify-app.jar' in:\n" + exeDir + "\n\nPlease ensure the application files are intact.",
                        "Clothify Store", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                string javaw = FindJavaw();
                if (string.IsNullOrEmpty(javaw))
                {
                    DialogResult res = MessageBox.Show(
                        "Java 11 or higher is required to run Clothify Store, but was not found on your system.\n\nWould you like to download it now for free from Adoptium?",
                        "Java Runtime Required - Clothify Store",
                        MessageBoxButtons.YesNo,
                        MessageBoxIcon.Warning);

                    if (res == DialogResult.Yes)
                    {
                        Process.Start("https://adoptium.net/temurin/releases/?version=11");
                    }
                    return;
                }

                ProcessStartInfo psi = new ProcessStartInfo();
                psi.FileName = javaw;
                psi.Arguments = "-Xms256m -Xmx1024m -jar \"" + jarPath + "\"";
                psi.WorkingDirectory = exeDir;
                psi.UseShellExecute = false;
                psi.CreateNoWindow = true;

                Process.Start(psi);
            }
            catch (Exception ex)
            {
                MessageBox.Show("Failed to launch Clothify Store:\n" + ex.Message,
                    "Error - Clothify Store", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        static string FindJavaw()
        {
            // 1. Try PATH
            string pathEnv = Environment.GetEnvironmentVariable("PATH") ?? "";
            string[] paths = pathEnv.Split(Path.PathSeparator);
            foreach (string p in paths)
            {
                try
                {
                    string candidate = Path.Combine(p.Trim(), "javaw.exe");
                    if (File.Exists(candidate)) return candidate;
                }
                catch {}
            }

            // 2. Try JAVA_HOME
            string javaHome = Environment.GetEnvironmentVariable("JAVA_HOME");
            if (!string.IsNullOrEmpty(javaHome))
            {
                string candidate = Path.Combine(javaHome, "bin", "javaw.exe");
                if (File.Exists(candidate)) return candidate;
            }

            // 3. Search common JDK locations
            string[] searchDirs = new string[]
            {
                @"C:\Program Files\Eclipse Adoptium",
                @"C:\Program Files\Java",
                @"C:\Program Files\Amazon Corretto",
                @"C:\Program Files\BellSoft",
                @"C:\Program Files\Microsoft\jdk",
                @"C:\Program Files (x86)\Java"
            };

            foreach (string baseDir in searchDirs)
            {
                if (Directory.Exists(baseDir))
                {
                    try
                    {
                        string[] files = Directory.GetFiles(baseDir, "javaw.exe", SearchOption.AllDirectories);
                        if (files.Length > 0) return files[0];
                    }
                    catch {}
                }
            }

            return null;
        }
    }
}
