from datetime import date, datetime
import pandas as pd

# Lista com os medicamentos que farão parte da blacklist. Esta blacklist ajuda a manter o foco do dataset removendo
# medicamentos que possívelmente trarão muito mais resultados que não são associados ao tema.
blackListMedicamentos = [
    "A SAÚDE DA MULHER",
    "ABBA",
    "ABC",
    "ACOG",
    "ADAPTA",
    "ALEXA",
    "AMORA",
    "AGUA PARA INJEÇÃO",
    "ARES",
    "AVAL",
    "BECA",
    "BETINA",
    "BIO E",
    "CEL",
    "CHERRY",
    "CICLO",
    "CIS",
    "COD",
    "CONCERTA",
    "DEJAVÚ",
    "DEX",
    "DON",
    "DRY",
    "DUAL",
    "DUETTO",
    "DUPLA",
    "ENJOY",
    "FERRO",
    "FIL",
    "FLUIR",
    "FRONTAL",
    "HALO",
    "HORA H",
    "IDA",
    "INGRID",
    "JAQUE",
    "JULIET",
    "KELLY",
    "LABEL",
    "LEVEL",
    "LIBRE",
    "LISTO",
    "LUNAH",
    "MALÚ",
    "MAYSA",
    "MEO",
    "MINIMA",
    "MIRADOR",
    "MYLLA",
    "NEXT",
    "NICE",
    "NIKI",
    "ÓPERA",
    "PACO",
    "PARÁ",
    "PISA"
    "PRÓS",
    "REC",
    "STER",
    "SISSY",
    "SUPREMA",
    "TUPI GUARANÁ",
    "VITAMINA E",
    "VIVENCIA",
    "YAZ",
    "YASMIN",
    "ZAP",
    "ÁGAPE",
    "ÁPICE",
    "ÉGIDE",
]

# Lê o arquivo de Medicamentos. Este arquivo é fornecido pela ANVISA no link abaixo
# e originalmente tem o nome "DADOS_ABERTOS_MEDICAMENTOS"
# https://dados.gov.br/dataset/medicamentos-registrados-no-brasil
df = pd.read_csv('Medicamentos.csv', sep=";", encoding="ISO-8859-15")

# Obtem as colunas que são relevantes para o processo, desse modo, simplificamos o processamento por ter menos dedos.
df = df[["NOME_PRODUTO","DATA_VENCIMENTO_REGISTRO"]]

#Altera a coluna de datas do Dataframe para facilitar comparação e limpeza
df["DATA_VENCIMENTO_REGISTRO"] = pd.to_datetime(df["DATA_VENCIMENTO_REGISTRO"], errors='coerce').astype('datetime64[ns]')

#Determina a função de limpeza que será usada no loc abaixo. Nesse caso, optamos por utilizar 
#apenas os medicamentos ativos, ou seja, aqueles em que a data do vencimento do registro é igual ou posterior a hoje.
funcao_limpeza = df["DATA_VENCIMENTO_REGISTRO"] >= pd.to_datetime('today')

#Limpa o dataframe utilizando a função de limpeza.
df_filtered = df.loc[funcao_limpeza]

funcao_limpeza = []
for a in df_filtered["NOME_PRODUTO"]:
    funcao_limpeza.append((' -') not in a and '%' not in a)

#Limpa o dataframe utilizando a função de limpeza.
df_filtered = df_filtered.loc[funcao_limpeza]

#Organiza o data frame por nome do medicamento em ordem alfabética crescente.
df_filtered.sort_values("NOME_PRODUTO", inplace = True)

#Elimina medicamentos com nomes duplicados.
df_filtered.drop_duplicates(subset ="NOME_PRODUTO", keep = False, inplace = True)

#Cria uma lista de fatores booleanos baseados na presença do medicamento na blacklist
boolSeries = ~df_filtered["NOME_PRODUTO"].isin(blackListMedicamentos)

#Remove os elementos presentes na blacklist
df_filtered = df_filtered[boolSeries]

#Exporta a lista filtrada de medicamentos para CSV
df_filtered.to_csv('../filtered_data.csv', index=False, header=False, encoding="ISO-8859-15")