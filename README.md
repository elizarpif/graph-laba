# ЛАБА №1 
## :exclamation: Образец правильного порядка команд :exclamation:
### При выполнении команды push может вылезти ошибка "*please tell me who you are*" - вам надо вбить команды git config --global user.email "yourmail@mail.ru" и также логин

*Для первого раза(для новой ветки)*
- git init
- git add README.md
- git commit - m "комментарий к изменениям"
- git checkout -b mybranch  
- git remote add origin https://github.com/elizarpif/graph-laba
- git push —set-upstream origin mybranch

*Для последующих разов*
- git add README.md
- git commit -m "changed README.md"
- git push