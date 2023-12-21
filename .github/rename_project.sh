#!/usr/bin/env bash
while getopts a:n:d: flag
do
    case "${flag}" in
        a) author=${OPTARG};;
        n) name=${OPTARG};;
        d) description=${OPTARG};;
    esac
done

name_snake=$(echo "$name" | sed -r -e 's/^([A-Z]+)([A-Z])/\L\1\E\2/' -e 's/^[A-Z]/\l&/' -e 's/[A-Z]+$/-\L&/' -e 's/([A-Z]+)([A-Z])/-\L\1\E\2/g' -e 's/[A-Z]/-\l&/g')

original_author="SettingDust"
original_name="FabricKotlinTemplate"
original_name_snake="fabric-kotlin-template"
original_url="https://github.com/SettingDust/FabricKotlinTemplate"
original_description="An example mod"
original_group="settingdust.template"
group="$(echo "$author" | tr '[:upper:]' '[:lower:]').$(echo "$name" | sed 's/ //g' | tr '[:upper:]' '[:lower:]')"
group_path="$(echo "$group" | tr '.' '/')"
url="https://github.com/$author/$name"

echo "Author: $author";
echo "Project Name: $name";
echo "Project Name Snake: $name_snake";
echo "Description: $description";
echo "Group: $group";
echo "Group Path: $group_path";
echo "Url: $url";

echo "Renaming project..."

# for filename in $(find . -name "*.*")
for filename in $(git ls-files -- ':!:.github/workflows*' ':!:.github/rename_project.sh')
do
  echo "sed -i s|$original_author|$author|g $filename"
    sed -i "s|$original_author|$author|g $filename"
  echo "sed -i s|$original_name|$name|g $filename"
    sed -i "s|$original_name|$name|g" "$filename"
  echo "sed -i s|$original_name_snake|$name_snake|g $filename"
    sed -i "s|$original_name_snake|$name_snake|g" "$filename"
  echo "sed -i s|$original_url|$url|g $filename"
    sed -i "s|$original_url|$url|g" "$filename"
  echo "sed -i s|$original_description|$description|g $filename"
    sed -i "s|$original_description|$description|g" "$filename"
  echo "sed -i s|$original_group|$group|g $filename"
    sed -i "s|$original_group|$group|g" "$filename"
    echo "Mapped $filename"
done

mv mod/src/client/java/settingdust/template "mod/src/client/java/$group_path"
mv mod/src/client/kotlin/settingdust/template "mod/src/client/kotlin/$group_path"
mv mod/src/client/resources/assets/fabric-kotlin-template "mod/src/client/resources/assets/$name_snake"
mv mod/src/client/resources/fabric-kotlin-template.client.mixins.json "mod/src/client/resources/$name_snake.client.mixins.json"
mv mod/src/main/java/settingdust/template "mod/src/main/java/$group_path"
mv mod/src/main/kotlin/settingdust/template "mod/src/main/kotlin/$group_path"
mv mod/src/main/resources/fabric-kotlin-template.mixins.json "mod/src/main/resources/$name_snake.mixins.json"
# This command runs only once on GHA!
rm -rf .github/template.yml
rm -rf .github/rename_project.sh
rm -rf .github/workflows/rename_project.yml
