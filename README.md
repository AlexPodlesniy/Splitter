# Splitter

Если вы часто ходите одной и той же компанией в кино, магазины, театры и т.д. и вам часто
лень пробивать все отдельными чеками, возможно у вас тоже возникает проблема подсчетов, кто кому сколько должен.
Ведь если сразу после совместных трат не рассчитываться, траты будут копиться и сосчитать их становится все сложнее.
Splitter - моя попытка решить эту проблему. Тут надо зарегистрировать вашу компанию, и когда кто-то платит за других,
он добавляет трату со своего аккаунта и сплиттер рассчитывает все долги за него.

Инструкция по запуску:
  1. Клонируете проект куда хотите.
  2. Качаете JDK последней версии (у меня вроде 11.0.6)
  3. Переходите в терминале в корневую папку проректа (<yourPath>/Splitter/)
  4. Вводите: $ java -classpath ./out/production/Splitter/ splitter.Splitter
  
  
Как пользоваться:
  1. При первом запуске вам предложат заполнить сплиттер тестовыми аккаунтами,
  советую это сделать если просто хотите посмотреть как все работает.
  2. Если вы это сделали:
    -- В консоль выведены логины и пароли пяти тестовых аккаунтов, выберайте любой и
    авторизуйтесь.
  3. Если вы этого не сделали:
    -- Cоздайте новый аккаунт.
    -- Т.к. вы не заполнили сплиттер аккаунтами, советую выйти из программы,
    выбрав опцию "exit" в главнном меню, и запустить заново, создав еще один аккаунт.
    Чтобы было еще веселее, повторите пару раз, чтобы аккаунтов стало от 3 до 5.
    
  4. Дальше вы попадаете в главное меню.
  В меню вы можете:
      1. проверить свой баланс: список людей которым вы должны, или 
      которые должны вам. Если сумма долга больше 0.0, значит должны вы, меньше 0.0 -
      должны вам.
      2. Добавить трату: вы оплатили общую трату. Введите описание, сумму, участников
      траты, и их доли.
      3. Расплатиться: вам выводится список долгов, если среди них есть долги с
      положительным значением (должны вы) выбираете этот долг и вводите сумму,
      которую перечислили заемщику.
      4. Вывести историю: выводит все совместные траты (дата, описание, кто оплатил,
      сумма, участники и их доли)
      5. Выход: сохраняет результат работы и закрывает приложение.
