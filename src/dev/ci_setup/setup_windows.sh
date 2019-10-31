mkdir -p "$HOME/.ssh"

if [[ "$USERPROFILE" = *".PACKER-"* ]]; then
  echo "####### Copying .ssh/ from $USERPROFILE to $HOME"

  cp -R "$USERPROFILE\\.ssh" "$HOME/.ssh"
fi

echo "####### Setting up GitHub known_hosts fingerprint..."

# Packer script adds this to known_hosts without a newline at the end of it
# So the first time a clone happens, a new entry is added for the specific IP address
# And it screws up the first line
GITHUB_FINGERPRINT="github.com ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAq2A7hRGmdnm9tUDbO9IDSwBK6TbQa+PXYPCPy6rbTrTtw7PHkccKrpp0yVhp5HdEIcKr6pLlVDBfOLX9QUsyCOV0wzfjIJNlGEYsdlLJizHhbn2mUjvSAHQqZETYP81eFzLQNnPHt4EVVUh7VfDESU84KezmD5QlWpXLmvU31/yMf+Se8xhHTvKSCZIFImWwoG6mbUoWf9nzpIoaSjB+weqqUUmpaaasXVal72J+UX2B+2RPW3RcT0eOzQgqlJL3RKrTJvdsjE3JEAvGq3lGHSZXy28G3skua2SmVi/w4yCE6gbODqnTWlg7+wC604ydGXA8VJiS5ap43JXiUFFAaQ=="
echo "$GITHUB_FINGERPRINT" > "$HOME/.ssh/known_hosts"
echo "$GITHUB_FINGERPRINT" > "$USERPROFILE\\.ssh\\known_hosts"

echo "####### Downloading IEDriverServer for WebDriver..."

IE_DRIVER_URL="https://selenium-release.storage.googleapis.com/3.14/IEDriverServer_x64_3.14.0.zip"
BIN_DIR="$HOME/bin"
mkdir -p "$BIN_DIR"

curl --silent -L -o "IEDriverServer.zip" "$IE_DRIVER_URL"
unzip -qo "IEDriverServer.zip" -d "$BIN_DIR"

#


# /c/Users/jenkins/.java/openjdk13/bin
# /mingw64/bin
# /usr/bin
# /c/Users/jenkins/bin
# /c/Program Files/ImageMagick-7.0.8-Q16
# /c/tools/ruby23/bin
# /c/ProgramData/Oracle/Java/javapath
# /c/Program Files (x86)/Common Files/Oracle/Java/javapath
# /c/Python27
# /c/Python27/Scripts
# /c/Windows/system32
# /c/Windows
# /c/Windows/System32/Wbem
# /c/Windows/System32/WindowsPowerShell/v1.0
# /c/Windows/System32/OpenSSH
# /c/ProgramData/GooGet
# /c/Program Files/Google/Compute Engine/metadata_scripts
# /c/Program Files (x86)/Google/Cloud SDK/google-cloud-sdk/bin
# /c/Program Files/Google/Compute Engine/sysprep
# /c/Program Files/infra/bin
# /c/ProgramData/chocolatey/bin
# /cmd
# /c/Program Files/Java/jdk1.8.0_221/bin
# /c/Program Files/CMake/bin
# /c/tools/mingw64/bin
# /c/Program Files/dotnet
# /c/Program Files/Microsoft SQL Server/130/Tools/Binn
# /c/ProgramData/nvm
# /c/Program Files/nodejs
# /c/Users/jenkins.PACKER-5DA68B40/AppData/Local/Microsoft/WindowsApps
