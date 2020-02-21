import requests

res = requests.get('https://us-central1-notify-15448.cloudfunctions.net/sendNotification?to={}&text={}&title={}'.format('taj4oEM','testo','titolo'))

print(res)