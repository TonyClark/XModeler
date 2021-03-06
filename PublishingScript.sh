CURDIR=`pwd`
TODIR=$1
WORKSPACE=$2/XModelerDistribution/target/products

mv $WORKSPACE/XModeler-macosx.cocoa.x86.zip $1
mv $WORKSPACE/XModeler-win32.win32.x86.zip $1
cd $1
git add -A
git commit -m "$(date +%s)_$RANDOM"
echo "Pushing new files to backup host"
#git remote add bitbucket ssh://git@bitbucket.org/xmodeler/xmodrel.git
git push -u bitbucket master
cd $CURDIR