import './App.css';
import { useEffect, useState } from 'react';


const OAUTH_CLIENT_ID = '838811690424-3dpv9tutkp89r1sbi41mrdkihuvruvsv.apps.googleusercontent.com';
const OAUTH_REDIRECT_URI = 'http://localhost:3000';

function generateCryptoRandomState() {
  const randomValues = new Uint32Array(2);
  window.crypto.getRandomValues(randomValues);

  // Encode as UTF-8
  const utf8Encoder = new TextEncoder();
  const utf8Array = utf8Encoder.encode(
    String.fromCharCode.apply(null, randomValues)
  );

  // Base64 encode the UTF-8 data
  return btoa(String.fromCharCode.apply(null, utf8Array))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
}

function signIn() {

  localStorage.setItem('oauth2-test-params', null);

  oauth2SignIn();
}

function oauth2SignIn() {
  // create random state value and store in local storage
  var state = generateCryptoRandomState();
  localStorage.setItem('state', state);

  var nonce = generateCryptoRandomState();

  // Google's OAuth 2.0 endpoint for requesting an access token
  var oauth2Endpoint = 'https://accounts.google.com/o/oauth2/v2/auth';

  // Create element to open OAuth 2.0 endpoint in new window.
  var form = document.createElement('form');
  form.setAttribute('method', 'GET'); // Send as a GET request.
  form.setAttribute('action', oauth2Endpoint);

  // Parameters to pass to OAuth 2.0 endpoint.
  var params = {
    'client_id': OAUTH_CLIENT_ID,
    'redirect_uri': OAUTH_REDIRECT_URI,
    'scope': 'openid email',
    'state': state,
    'nonce': nonce,
    'response_type': 'id_token'
  };

  // Add form parameters as hidden input values.
  for (var p in params) {
    var input = document.createElement('input');
    input.setAttribute('type', 'hidden');
    input.setAttribute('name', p);
    input.setAttribute('value', params[p]);
    form.appendChild(input);
  }

  // Add form to page and submit it to open the OAuth 2.0 endpoint.
  document.body.appendChild(form);
  form.submit();
}


function App() {

  const [displayMsg, setDisplayMsg] = useState('Please, sign in to get ID Token');
  const [idToken, setIdToken] = useState('');
  const [tokenInfo, setTokenInfo] = useState('');

  const [tokenInfoRequestAbortController, setTokenInfoRequestAbortController] = useState(null);

  useEffect(() => {

    const fragmentString = window.location.hash.substring(1);
    window.location.hash = '';

    let params = JSON.parse(localStorage.getItem('oauth2-test-params')) ?? {};

    if (fragmentString !== '') {

      let regex = /([^&=]+)=([^&]*)/g, m;

      while (m = regex.exec(fragmentString)) {
        params[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
      }

      if (Object.keys(params).length > 0 && params['state']) {
        if (params['state'] == localStorage.getItem('state')) {
          localStorage.setItem('oauth2-test-params', JSON.stringify(params));
          setDisplayMsg('Successfully signed in');
        } else {
          setDisplayMsg('State mismatch. Possible CSRF attack');
          return;
        }
      }
    }

    if (localStorage.getItem('oauth2-test-params') !== null) {

      setDisplayMsg('')

      if (params && String(params.id_token)) {

        setIdToken(params.id_token);

        async function initTokenInfo() {

          if (tokenInfoRequestAbortController !== null) {
            tokenInfoRequestAbortController.abort();
          }
          setTokenInfoRequestAbortController(new AbortController());

          let response = null;
          try {
            response = await fetch(
              `https://oauth2.googleapis.com/tokeninfo?id_token=${params.id_token}`
            );
          } catch (error) {
            return;
          }

          let tokenInfoResponse = await response.json();

          console.log(JSON.stringify(tokenInfoResponse));

          if (tokenInfoResponse !== null) {
            setTokenInfo(JSON.stringify(tokenInfoResponse, null, 2));
          } else {
            setTokenInfo('An error occured when trying to get token info');
          }


        }

        initTokenInfo();
      }

    }
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <pre>
          <p>{displayMsg}</p>
          {idToken === '' ? '' :
            (<>
              <h2>Current ID Token to copy:</h2>&nbsp;
              <textarea defaultValue={idToken} readOnly></textarea>

              <p><button className="button-54" role="button" onClick={signIn}>Re-Authenticate</button></p>
              <p>ID Token info: {tokenInfo}</p>
            </>)
          }
        </pre>
        {idToken !== '' ? '' : (<button className="button-54" role="button" onClick={signIn}>Authenticate </button>)}

      </header>
    </div>
  );
}

export default App;
