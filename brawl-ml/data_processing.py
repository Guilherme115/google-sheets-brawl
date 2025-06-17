import gspread
from oauth2client.service_account import ServiceAccountCredentials
import pandas as pd


def ler_planilhas_google(nome_planilha:str, arquivo_credencial:str, aba: int =0) -> pd.DataFrame:
    scope = ["https://spreadsheets.google.com/feeds",
             "https://www.googleapis.com/auth/drive"]
    creds = ServiceAccountCredentials.from_json_keyfile_name(arquivo_credencial, scope)
    client = gspread.authorize(creds)
    planilha = client.open(nome_planilha)
    worksheet = planilha.get_worksheet(aba)
    dados = worksheet.get_all_records()
    df = pd.DataFrame(dados)
    return df



if __name__ == "__main__":
    # Exemplo rápido para testar
    nome = "Nome da sua planilha aqui"
    cred = "credenciais.json"
    df = ler_planilhas_google(nome, cred)
    print(df.head())
                                                   
                                                