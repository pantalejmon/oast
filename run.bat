

echo Kompilacja...
call gradlew clean
call gradlew build
call gradlew jar
echo Uruchomienie
call gradlew run
Rem  Jakby gradle nie zadziałał to można tradycyjnie call java -jar build/libs/"OAST projekt.jar"
