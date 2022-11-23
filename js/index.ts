iimport * as fs from "fs";
import data from "./bid.json";
import { Module } from "./types";

const data = await response.json() */

const getBidList = (data: Module[]): string[] => {
  const bidList = data.reduce((pre: string[], cur) => {
    const bidFromEvents = cur.events.map((event) => event.bid);
    const bidFromSubModules = cur.subModules.map((subModule) => subModule.events[0].bid);
    pre.push(...bidFromEvents, ...bidFromSubModules);
    return pre;
  }, []);
  return bidList;
};
const getIntersection = (arr1: string[], arr2: string[]) => {
  return arr1.filter((item) => arr2.includes(item));
};
const bidListFromOcean = getBidList(data.data.pages[0].modules);

function readData(err: any, data: string) {
  const bidListFromFile = data.match(/b_(\S+)m(c|v)/g) || [];
  const intersection = getIntersection(bidListFromFile, bidListFromOcean);
  if (intersection.length < bidListFromFile.length) {
    throw new Error("当前文件存在错误的 bid！");
  } else {
    console.log("bid 验证通过!");
  }
}

fs.readFile("./src/example/App.vue", "utf8", readData);
