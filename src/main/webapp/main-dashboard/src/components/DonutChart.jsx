import React from 'react';
import Chart from 'react-apexcharts';

const DonutChart = ({ data }) => {
  const chartOptions = {
    labels: data.map((labels)=> labels.name),
    
    dataLabels: {
      enabled: false
    },
    
    plotOptions: {
      pie: {
        donut: {
          size: '60%'
        },
      },

    responsive: [{breakpoint: 300, options: {chart: {width: 250},}}]
    },

    legend: {
          position: 'right',
          formatter: function(val, opts) {return `${val} - ${opts.w.globals.series[opts.seriesIndex]}%`}},
  };


  const chartSeries = data.map((it) => it.total);

  return (
    <Chart
      options={chartOptions}
      series={chartSeries}
      type="donut"
      width="460"
    />
  );
};

export default DonutChart;
