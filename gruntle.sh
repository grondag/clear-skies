readonly MC_VERSION="1.18"

echo "GRUNTLE REFRESH FOR $MC_VERSION - IF THIS IS NOT A $MC_VERSION BRANCH YOU HAVE DONE A BAD"

if [[ $1 == 'auto' ]]; then
  if ! grep -q gruntle .gitignore; then
    echo "Auto-update requires .gitignore to exclude the gruntle folder. Please update .gitignore and retry."
    exit 1
  fi

  if output=$(git status --porcelain) && [ -z "$output" ]; then
    echo "Attempting auto-update. Git starting status is clean."
  else
    echo "Auto-update requires clean git status. Please commit or stash changes and retry."
    exit 1
  fi
fi

echo 'Checking for build updates...'
# delete gruntle repo folder if exists from aborted run
if [ -d "gruntle-master" ]; then
  rm -rf gruntle-master
fi

# download and unpack latest gruntle bundle
curl https://github.com/vram-guild/gruntle/archive/refs/heads/master.zip -sSOJL
unzip -q gruntle-master

# copy content for our branch and then remove bundle
# this handles simple, file-based updates: checkstyle, standard gradle configs, etc.
cp -R gruntle-master/$MC_VERSION/ .
rm -rf gruntle-master
rm gruntle-master.zip

# run latest refresh
source gruntle/refresh.sh

# remove scripts
rm -rf gruntle

echo 'Gruntle refresh complete'
