FROM apache/airflow:2.9.2
USER airflow
ENV PATH="/home/airflow/.local/bin:$PATH"
RUN pip install --no-cache-dir \
    apache-airflow-providers-oracle==3.10.0 \
    oracledb \
    python-dotenv