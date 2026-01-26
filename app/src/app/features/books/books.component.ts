import { Component, OnInit } from '@angular/core';
import { BookService } from '@app/core';
import { BookModel } from '@app/core';
import {CommonModule, NgForOf} from "@angular/common";
import {PageFooterComponent, PageLayoutComponent} from "@app/shared";

@Component({
  selector: 'app-books',
  templateUrl: './books.component.html',
  standalone: true,
  imports: [PageLayoutComponent,CommonModule],
})
export class BooksComponent implements OnInit {
  books: BookModel[] = [];
  error: any = null;

  constructor(public bookService: BookService) {}

  ngOnInit(): void {
    console.log('ngOnInit running')
    this.bookService.getBooks().subscribe((response:any) => {


      const { data, error } = response;
      if (data) {

        // console.log(data.content)
        this.books=data.content;

        // this.books = data as BookModel[];

      }
      if (error) {
        this.error = error;
      }
      console.log('end of ngoninit')
    });
  }
}