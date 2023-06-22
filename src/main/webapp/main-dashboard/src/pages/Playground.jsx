import ChartContainer from "../components/ChartContainer"
import ChartLine from "../components/ChartLine"
import ChartBar from "../components/ChartBar"
import { Table } from "../components/Table"
import { dataB } from "../../public/mockData"

function Playground() {
  return (
    <section className="flex flex-col gap-4">
      <Table dataList={dataB}/>
      <section className="w-full flex flex-wrap xl:flex-nowrap gap-6 ">
        <ChartContainer chart={<ChartBar/>} type={'bar'}>
        </ChartContainer>
        <ChartContainer chart={<ChartLine/>} type={'line'}>
        </ChartContainer>
      </section>
    </section>
  )
}

export default Playground