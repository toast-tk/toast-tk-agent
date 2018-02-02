if [ ${TRAVIS_PULL_REQUEST} = 'false' ] && [[ $TRAVIS_BRANCH = 'master'  ||  ${TRAVIS_BRANCH} = 'snapshot' ]]; then
      echo 'Checking Quality Gates against sonar'
      mvn --batch-mode clean -U verify org.sonarsource.scanner.maven:sonar-maven-plugin:3.4.0.905:sonar -Dsonar.host.url=${SONAR_URL} -Dsonar.login=${SONAR_LOGIN} -Dsonar.organization=${SONAR_ORGANIZATION} --settings ./settings.xml;
elif [ ${TRAVIS_PULL_REQUEST} != 'false' ]; then
      echo 'Build and analyze pull request'
      mvn --batch-mode clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:3.4.0.905:sonar -Dsonar.analysis.mode=issues -Dsonar.host.url=${SONAR_URL} -Dsonar.login=${SONAR_LOGIN} -Dsonar.organization=${SONAR_ORGANIZATION} -Dsonar.github.oauth=${SONAR_GITHUB_OAUTH} -Dsonar.github.repository=toast-tk/toast-tk-agent -Dsonar.github.pullRequest=${TRAVIS_PULL_REQUEST};
fi